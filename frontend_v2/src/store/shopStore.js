import { reactive, computed } from 'vue'

const state = reactive({
  currentUserId: null,
  currentAdminId: null,
  currentUser: null,
  currentAdmin: null,
  categories: [],
  products: [],
  users: [],
  addresses: [],
  cart: [],
  orders: [],
  reviews: [],
  buyNow: {
    active: false,
    backupCart: null
  }
})

const USER_TOKEN_KEY = 'snack_store_user_token'
const ADMIN_TOKEN_KEY = 'snack_store_admin_token'

const getUserToken = () => localStorage.getItem(USER_TOKEN_KEY) || ''
const setUserToken = (token) => {
  if (!token) localStorage.removeItem(USER_TOKEN_KEY)
  else localStorage.setItem(USER_TOKEN_KEY, token)
}
const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY) || ''
const setAdminToken = (token) => {
  if (!token) localStorage.removeItem(ADMIN_TOKEN_KEY)
  else localStorage.setItem(ADMIN_TOKEN_KEY, token)
}

const API_BASE = (import.meta.env?.VITE_API_BASE || '').replace(/\/$/, '')

const toNumber = (value) => {
  const n = Number(value)
  return Number.isFinite(n) ? n : null
}

const normalizeDateTime = (value) => {
  if (!value) return ''
  const s = String(value)
  if (s.includes('T')) return s.replace('T', ' ').slice(0, 16)
  return s.slice(0, 16)
}

const normalizeCategory = (c) => ({
  id: String(c?.id ?? ''),
  name: c?.name ?? '',
  status: c?.status ?? ''
})

const normalizeProduct = (p) => {
  const images = Array.isArray(p?.images) ? p.images.filter(Boolean) : []
  const image = p?.image || p?.mainImage || images[0] || ''
  return {
    id: String(p?.id ?? ''),
    name: p?.name ?? '',
    price: Number(p?.price ?? 0),
    stock: Number(p?.stock ?? 0),
    status: p?.status ?? '',
    categoryId: String(p?.categoryId ?? ''),
    createdAt: normalizeDateTime(p?.createdAt),
    updatedAt: normalizeDateTime(p?.updatedAt),
    image,
    images: images.length ? images : image ? [image] : [],
    brand: p?.brand ?? '',
    shipFrom: p?.shipFrom ?? '',
    origin: p?.origin ?? '',
    sku: p?.sku ?? '',
    specs: p?.specs || null,
    description: p?.description ?? '',
    categoryName: p?.categoryName ?? ''
  }
}

const normalizeUser = (u) => ({
  id: String(u?.id ?? ''),
  username: u?.username ?? '',
  phone: u?.phone ?? '',
  email: u?.email ?? '',
  status: u?.status ?? ''
})

const normalizeAddress = (a) => ({
  id: String(a?.id ?? ''),
  userId: String(a?.userId ?? ''),
  name: a?.name ?? '',
  phone: a?.phone ?? '',
  region: a?.region ?? '',
  detail: a?.detail ?? '',
  isDefault: Boolean(a?.isDefault)
})

const normalizeCartItem = (it) => ({
  id: String(it?.id ?? ''),
  productId: String(it?.productId ?? ''),
  quantity: Number(it?.quantity ?? 0),
  price: Number(it?.price ?? 0),
  productName: it?.productName ?? '',
  productImage: it?.productImage ?? '',
  productStock: it?.productStock ?? 0,
  productStatus: it?.productStatus ?? ''
})

const normalizeOrderView = (ov) => {
  const o = ov?.order || {}
  const items = Array.isArray(ov?.items) ? ov.items : []
  return {
    id: String(o?.id ?? ''),
    orderNo: o?.orderNo ?? '',
    userId: String(o?.userId ?? ''),
    status: o?.status ?? '',
    amount: Number(o?.amount ?? 0),
    createdAt: normalizeDateTime(o?.createdAt),
    address: {
      name: o?.receiverName ?? '',
      phone: o?.receiverPhone ?? '',
      region: o?.receiverRegion ?? '',
      detail: o?.receiverDetail ?? ''
    },
    items: items.map((it) => ({
      productId: String(it?.productId ?? ''),
      productName: it?.productName ?? '',
      productImage: it?.productImage ?? '',
      quantity: Number(it?.quantity ?? 0),
      price: Number(it?.price ?? 0)
    })),
    shipment: ov?.shipment || null
  }
}

const apiRequest = async (path, options = {}) => {
  const method = options.method || 'GET'
  const body = options.body
  const auth = options.auth || null
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  const token = auth === 'user' ? getUserToken() : auth === 'admin' ? getAdminToken() : ''
  if (token) headers.Authorization = `Bearer ${token}`

  const resp = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body)
  })
  const payload = await resp.json().catch(() => null)
  if (!resp.ok) {
    const msg = payload?.message || `HTTP ${resp.status}`
    throw new Error(msg)
  }
  if (payload && typeof payload.code === 'number') {
    if (payload.code !== 0) throw new Error(payload.message || '请求失败')
    return payload.data
  }
  return payload
}

const cartItems = computed(() =>
  state.cart
    .map((item) => {
      const product = state.products.find((p) => p.id === item.productId)
      const p =
        product ||
        (item.productId
          ? {
              id: item.productId,
              name: item.productName || '商品',
              image: item.productImage || '',
              stock: item.productStock || 0,
              status: item.productStatus || '上架',
              price: item.price || 0
            }
          : null)
      if (!p) return null
      return {
        ...item,
        product: p,
        subtotal: Number(((Number(p.price) || 0) * item.quantity).toFixed(2))
      }
    })
    .filter(Boolean)
)

const cartCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const cartTotal = computed(() => Number(cartItems.value.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2)))

const currentUser = computed(() => state.currentUser || null)
const currentAdmin = computed(() => state.currentAdmin || null)

const currentUserAddresses = computed(() =>
  state.addresses.filter((addr) => addr.userId === state.currentUserId)
)

const currentUserOrders = computed(() =>
  state.orders.filter((order) => order.userId === state.currentUserId)
)

const refreshCategories = async () => {
  const list = await apiRequest('/api/categories')
  state.categories = (Array.isArray(list) ? list : []).map(normalizeCategory)
}

const refreshProducts = async () => {
  const isAdmin = Boolean(state.currentAdminId && getAdminToken())
  const list = isAdmin ? await apiRequest('/api/admin/products', { auth: 'admin' }) : await apiRequest('/api/products')
  state.products = (Array.isArray(list) ? list : []).map(normalizeProduct)
}

const refreshCart = async () => {
  if (!state.currentUserId || !getUserToken()) {
    state.cart = []
    return
  }
  const list = await apiRequest('/api/cart', { auth: 'user' })
  state.cart = (Array.isArray(list) ? list : []).map(normalizeCartItem)
}

const refreshAddresses = async () => {
  if (!state.currentUserId || !getUserToken()) {
    state.addresses = []
    return
  }
  const list = await apiRequest('/api/addresses', { auth: 'user' })
  state.addresses = (Array.isArray(list) ? list : []).map(normalizeAddress)
}

const refreshOrders = async () => {
  const isAdmin = Boolean(state.currentAdminId && getAdminToken())
  if (!isAdmin && (!state.currentUserId || !getUserToken())) {
    state.orders = []
    return
  }
  const list = isAdmin ? await apiRequest('/api/admin/orders', { auth: 'admin' }) : await apiRequest('/api/orders', { auth: 'user' })
  state.orders = (Array.isArray(list) ? list : []).map(normalizeOrderView)
}

const refreshUsers = async () => {
  if (!state.currentAdminId || !getAdminToken()) {
    state.users = []
    return
  }
  const list = await apiRequest('/api/admin/users', { auth: 'admin' })
  state.users = (Array.isArray(list) ? list : []).map(normalizeUser)
}

const fetchProductDetail = async (productId) => {
  const id = toNumber(productId)
  if (!id) return null
  const detail = await apiRequest(`/api/products/${id}`)
  const normalized = normalizeProduct(detail)
  const idx = state.products.findIndex((p) => p.id === normalized.id)
  if (idx >= 0) state.products[idx] = normalized
  else state.products.unshift(normalized)
  return normalized
}

const fetchFeaturedProducts = async (limit = 3) => {
  const safe = Math.max(1, Math.min(Number(limit) || 3, 20))
  const list = await apiRequest(`/api/products/featured?limit=${safe}`)
  return (Array.isArray(list) ? list : []).map(normalizeProduct)
}

const fetchNewArrivals = async (limit = 3) => {
  const safe = Math.max(1, Math.min(Number(limit) || 3, 20))
  const list = await apiRequest(`/api/products/new-arrivals?limit=${safe}`)
  return (Array.isArray(list) ? list : []).map(normalizeProduct)
}

const addToCart = async (productId, quantity = 1) => {
  const product = state.products.find((p) => p.id === productId)
  if (!product || product.status !== '上架' || product.stock <= 0) return false
  const addCount = Number(quantity)
  if (!Number.isFinite(addCount) || addCount <= 0) return false
  if (!state.currentUserId || !getUserToken()) return false
  await apiRequest('/api/cart/items', {
    method: 'POST',
    auth: 'user',
    body: { productId: toNumber(productId), quantity: Math.min(addCount, product.stock) }
  })
  await refreshCart()
  return true
}

const startBuyNow = async (productId, quantity = 1) => {
  const product = state.products.find((p) => p.id === productId)
  if (!product || product.status !== '上架' || product.stock <= 0) return false
  const nextQuantity = Number(quantity)
  if (!Number.isFinite(nextQuantity) || nextQuantity <= 0) return false
  if (!state.currentUserId || !getUserToken()) return false
  await refreshCart()
  state.buyNow.backupCart = state.cart.map((i) => ({ ...i }))
  state.buyNow.active = true
  await apiRequest('/api/cart/clear', { method: 'POST', auth: 'user' })
  await apiRequest('/api/cart/items', {
    method: 'POST',
    auth: 'user',
    body: { productId: toNumber(productId), quantity: Math.min(nextQuantity, product.stock) }
  })
  await refreshCart()
  return true
}

const cancelBuyNow = async () => {
  if (!state.buyNow.active) return
  if (!state.currentUserId || !getUserToken()) return
  const backup = Array.isArray(state.buyNow.backupCart) ? state.buyNow.backupCart.map((i) => ({ ...i })) : []
  await apiRequest('/api/cart/clear', { method: 'POST', auth: 'user' })
  for (const it of backup) {
    await apiRequest('/api/cart/items', {
      method: 'POST',
      auth: 'user',
      body: { productId: toNumber(it.productId), quantity: Number(it.quantity) || 1 }
    })
  }
  await refreshCart()
  state.buyNow.active = false
  state.buyNow.backupCart = null
}

const updateCartQuantity = async (productId, quantity) => {
  const product = state.products.find((p) => p.id === productId)
  if (!product) return
  const item = state.cart.find((c) => c.productId === productId)
  if (!item) {
    await refreshCart()
    return
  }
  const nextQuantity = Number(quantity)
  if (!Number.isFinite(nextQuantity)) return
  const safeQuantity = Math.max(1, Math.min(nextQuantity, product.stock))
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest(`/api/cart/items/${toNumber(item.id)}`, {
    method: 'PUT',
    auth: 'user',
    body: { quantity: safeQuantity }
  })
  await refreshCart()
}

const removeFromCart = async (productId) => {
  const item = state.cart.find((c) => c.productId === productId)
  if (!item) {
    await refreshCart()
    return
  }
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest(`/api/cart/items/${toNumber(item.id)}`, { method: 'DELETE', auth: 'user' })
  await refreshCart()
}

const clearCart = async () => {
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest('/api/cart/clear', { method: 'POST', auth: 'user' })
  await refreshCart()
}

const loginUser = async (phone, password) => {
  const resp = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: { phone, password }
  })
  const token = resp?.token || ''
  const user = resp?.user || null
  if (!token || !user) return false
  setUserToken(token)
  state.currentUserId = String(user.id)
  state.currentUser = { ...normalizeUser(user), avatar: '' }
  await refreshAddresses()
  await refreshCart()
  await refreshOrders()
  return true
}

const registerUser = async (payload) => {
  const resp = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: { username: payload.username, phone: payload.phone, email: payload.email, password: payload.password }
  })
  const token = resp?.token || ''
  const user = resp?.user || null
  if (!token || !user) return null
  setUserToken(token)
  state.currentUserId = String(user.id)
  state.currentUser = { ...normalizeUser(user), avatar: payload.avatar || '' }
  await refreshAddresses()
  await refreshCart()
  await refreshOrders()
  return state.currentUser
}

const updateUserProfile = async (payload) => {
  if (!state.currentUserId || !getUserToken()) return
  const updated = await apiRequest('/api/me', {
    method: 'PUT',
    auth: 'user',
    body: { username: payload.username, email: payload.email }
  })
  const next = normalizeUser(updated)
  state.currentUser = { ...state.currentUser, ...next, avatar: payload.avatar ?? state.currentUser?.avatar ?? '' }
}

const logoutUser = async () => {
  const token = getUserToken()
  if (token) {
    await apiRequest('/api/auth/logout', { method: 'POST', auth: 'user' }).catch(() => {})
  }
  setUserToken('')
  state.currentUserId = null
  state.currentUser = null
  state.addresses = []
  state.cart = []
  state.orders = []
}

const loginAdmin = async (username, password) => {
  const resp = await apiRequest('/api/admin/auth/login', {
    method: 'POST',
    body: { username, password }
  })
  const token = resp?.token || ''
  if (!token) return false
  setAdminToken(token)
  state.currentAdminId = String(resp?.adminId || '')
  state.currentAdmin = { id: String(resp?.adminId || ''), username: resp?.username || username }
  await refreshUsers()
  await refreshProducts()
  await refreshOrders()
  return true
}

const logoutAdmin = async () => {
  setAdminToken('')
  state.currentAdminId = null
  state.currentAdmin = null
  state.users = []
  await refreshProducts()
  await refreshOrders()
}

const addAddress = async (payload) => {
  if (!state.currentUserId || !getUserToken()) return null
  await apiRequest('/api/addresses', {
    method: 'POST',
    auth: 'user',
    body: {
      name: payload.name,
      phone: payload.phone,
      region: payload.region,
      detail: payload.detail,
      isDefault: Boolean(payload.isDefault)
    }
  })
  await refreshAddresses()
  return state.addresses[0] || null
}

const updateAddress = async (id, payload) => {
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest(`/api/addresses/${toNumber(id)}`, {
    method: 'PUT',
    auth: 'user',
    body: {
      name: payload.name,
      phone: payload.phone,
      region: payload.region,
      detail: payload.detail,
      isDefault: Boolean(payload.isDefault)
    }
  })
  await refreshAddresses()
}

const removeAddress = async (id) => {
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest(`/api/addresses/${toNumber(id)}`, { method: 'DELETE', auth: 'user' })
  await refreshAddresses()
}

const setDefaultAddress = async (id) => {
  if (!state.currentUserId || !getUserToken()) return
  await apiRequest(`/api/addresses/${toNumber(id)}/default`, { method: 'POST', auth: 'user' })
  await refreshAddresses()
}

const createOrder = async (payload) => {
  if (!state.currentUserId || !getUserToken()) return null
  const created = await apiRequest('/api/orders', {
    method: 'POST',
    auth: 'user',
    body: { addressId: toNumber(payload.addressId) }
  })
  const order = normalizeOrderView(created)
  await refreshOrders()
  await refreshCart()
  state.buyNow.active = false
  state.buyNow.backupCart = null
  return order
}

const updateOrderStatus = async (orderId, status) => {
  const isAdmin = Boolean(state.currentAdminId && getAdminToken())
  if (isAdmin) {
    await apiRequest(`/api/admin/orders/${toNumber(orderId)}/status`, {
      method: 'POST',
      auth: 'admin',
      body: { status }
    })
    await refreshOrders()
    return
  }
  if (!state.currentUserId || !getUserToken()) return
  if (status === '已取消') {
    await apiRequest(`/api/orders/${toNumber(orderId)}/cancel`, { method: 'POST', auth: 'user' })
    await refreshOrders()
    return
  }
  if (status === '已完成') {
    await apiRequest(`/api/orders/${toNumber(orderId)}/confirm`, { method: 'POST', auth: 'user' })
    await refreshOrders()
  }
}

const getProductReviews = (productId) => state.reviews.filter((r) => r.productId === productId)

const getProductReviewSummary = (productId) => {
  const list = getProductReviews(productId)
  const count = list.length
  const average = count ? Number((list.reduce((sum, r) => sum + r.rating, 0) / count).toFixed(1)) : 0
  return { count, average }
}

const hasPurchasedProduct = (productId) => {
  if (!state.currentUserId) return false
  return state.orders.some(
    (o) =>
      o.userId === state.currentUserId &&
      o.status !== '已取消' &&
      o.items.some((it) => it.productId === productId)
  )
}

const canReviewProduct = (productId) => {
  if (!state.currentUserId) return false
  const hasCompletedPurchase = state.orders.some(
    (o) =>
      o.userId === state.currentUserId &&
      o.status === '已完成' &&
      o.items.some((it) => it.productId === productId)
  )
  if (!hasCompletedPurchase) return false
  const hasReviewed = state.reviews.some(
    (r) => r.productId === productId && r.userId === state.currentUserId
  )
  return !hasReviewed
}

const addProductReview = (productId, payload) => {
  if (!canReviewProduct(productId)) return false
  const rating = Number(payload?.rating)
  if (!Number.isFinite(rating) || rating < 1 || rating > 5) return false
  const content = String(payload?.content || '').trim()
  if (!content) return false
  const review = {
    id: `r${Date.now()}`,
    productId,
    userId: state.currentUserId,
    rating,
    content: content.slice(0, 240),
    createdAt: new Date().toISOString().slice(0, 16).replace('T', ' ')
  }
  state.reviews.unshift(review)
  return true
}

const updateProduct = (productId, payload) => {
  return updateProductAsync(productId, payload)
}

const updateProductAsync = async (productId, payload) => {
  if (!state.currentAdminId || !getAdminToken()) return null
  const images = Array.isArray(payload.images) ? payload.images.filter(Boolean).slice(0, 4) : []
  const image = images.length ? images[0] : payload.image || ''
  await apiRequest(`/api/admin/products/${toNumber(productId)}`, {
    method: 'PUT',
    auth: 'admin',
    body: {
      name: payload.name,
      price: Number(payload.price),
      stock: Number(payload.stock),
      categoryId: toNumber(payload.categoryId),
      status: payload.status,
      image,
      mainImage: image,
      brand: payload.brand || '',
      shipFrom: payload.shipFrom || '',
      origin: payload.origin || '',
      sku: payload.sku || '',
      description: payload.description || '',
      images,
      specs: payload.specs || {}
    }
  })
  await refreshProducts()
  return state.products.find((p) => p.id === String(productId)) || null
}

const addProduct = async (payload) => {
  if (!state.currentAdminId || !getAdminToken()) return null
  const images = Array.isArray(payload.images) ? payload.images.filter(Boolean).slice(0, 4) : []
  const image = images.length ? images[0] : payload.image || ''
  const created = await apiRequest('/api/admin/products', {
    method: 'POST',
    auth: 'admin',
    body: {
      name: payload.name,
      price: Number(payload.price),
      stock: Number(payload.stock),
      categoryId: toNumber(payload.categoryId),
      status: payload.status,
      image,
      mainImage: image,
      brand: payload.brand || '',
      shipFrom: payload.shipFrom || '',
      origin: payload.origin || '',
      sku: payload.sku || '',
      description: payload.description || '',
      images,
      specs: payload.specs || {}
    }
  })
  const normalized = normalizeProduct(created)
  const idx = state.products.findIndex((p) => p.id === normalized.id)
  if (idx >= 0) state.products[idx] = normalized
  else state.products.unshift(normalized)
  return normalized
}

const updateUserStatus = async (userId, status) => {
  if (!state.currentAdminId || !getAdminToken()) return
  await apiRequest(`/api/admin/users/${toNumber(userId)}/status`, {
    method: 'PUT',
    auth: 'admin',
    body: { status }
  })
  await refreshUsers()
}

const updateUser = async (userId, payload) => {
  if (!state.currentAdminId || !getAdminToken()) return null
  const updated = await apiRequest(`/api/admin/users/${toNumber(userId)}`, {
    method: 'PUT',
    auth: 'admin',
    body: {
      username: payload?.username ?? '',
      phone: payload?.phone ?? '',
      email: payload?.email ?? '',
      status: payload?.status ?? ''
    }
  })
  const normalized = normalizeUser(updated)
  const idx = state.users.findIndex((u) => u.id === normalized.id)
  if (idx >= 0) state.users[idx] = normalized
  else state.users.unshift(normalized)
  return normalized
}

const bootstrap = async () => {
  await refreshCategories().catch(() => {})
  await refreshProducts().catch(() => {})

  const userToken = getUserToken()
  if (userToken) {
    const me = await apiRequest('/api/me', { auth: 'user' }).catch(() => null)
    if (me) {
      state.currentUserId = String(me?.id ?? '')
      state.currentUser = { ...normalizeUser(me), avatar: '' }
      await refreshAddresses().catch(() => {})
      await refreshCart().catch(() => {})
      await refreshOrders().catch(() => {})
    } else {
      setUserToken('')
    }
  }

  const adminToken = getAdminToken()
  if (adminToken) {
    const ok = await apiRequest('/api/admin/users', { auth: 'admin' })
      .then(() => true)
      .catch(() => false)
    if (ok) {
      state.currentAdminId = '1'
      state.currentAdmin = { id: '1', username: 'admin' }
      await refreshUsers().catch(() => {})
      await refreshProducts().catch(() => {})
      await refreshOrders().catch(() => {})
    } else {
      setAdminToken('')
    }
  }
}

const bootstrapDone = bootstrap().catch(() => {})

export const useShopStore = () => ({
  state,
  bootstrapDone,
  cartItems,
  cartCount,
  cartTotal,
  currentUser,
  currentAdmin,
  currentUserAddresses,
  currentUserOrders,
  refreshCategories,
  refreshProducts,
  refreshCart,
  refreshAddresses,
  refreshOrders,
  refreshUsers,
  fetchProductDetail,
  fetchFeaturedProducts,
  fetchNewArrivals,
  addToCart,
  startBuyNow,
  cancelBuyNow,
  updateCartQuantity,
  removeFromCart,
  clearCart,
  loginUser,
  registerUser,
  updateUserProfile,
  logoutUser,
  loginAdmin,
  logoutAdmin,
  addAddress,
  updateAddress,
  removeAddress,
  setDefaultAddress,
  createOrder,
  updateOrderStatus,
  getProductReviews,
  getProductReviewSummary,
  hasPurchasedProduct,
  canReviewProduct,
  addProductReview,
  updateProduct,
  addProduct,
  updateUserStatus,
  updateUser
})
