<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const route = useRoute()
const currentUser = store.currentUser
const cartCount = store.cartCount
const activeCategory = ref('all')
const keyword = ref(typeof route.query.q === 'string' ? route.query.q : '')
const browseRef = ref(null)
const pageSize = 12
const currentPage = ref(1)
const toastText = ref('')
const toastVisible = ref(false)

let toastTimer = null

const showToast = (text) => {
  toastText.value = text
  toastVisible.value = true
  if (toastTimer) window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toastVisible.value = false
    toastTimer = null
  }, 1600)
}

onUnmounted(() => {
  if (toastTimer) window.clearTimeout(toastTimer)
})

const scrollToBrowse = async () => {
  await nextTick()
  const el = browseRef.value
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - 92
  window.scrollTo({ top: Math.max(0, top), behavior: 'smooth' })
}

watch(
  () => route.query.q,
  (next) => {
    keyword.value = typeof next === 'string' ? next : ''
    const q = typeof next === 'string' ? next.trim() : ''
    currentPage.value = 1
    if (q) scrollToBrowse()
  }
)

const popularSearches = ['开心果', '牛肉干', '巧克力', '薯片', '曲奇', '抹茶']

const categoryMeta = {
  c1: {
    title: '坚果炒货',
    icon: 'M12 2a10 10 0 1 0 0 20a10 10 0 0 0 0-20Zm0 4a6 6 0 0 1 6 6c0 3.314-2.686 6-6 6s-6-2.686-6-6a6 6 0 0 1 6-6Z'
  },
  c2: {
    title: '糖巧零食',
    icon: 'M7 6a5 5 0 0 1 10 0v2h1a2 2 0 0 1 2 2v2a4 4 0 0 1-4 4h-2.2l-.8 2.4a1 1 0 0 1-1.9 0L10.4 18H8a4 4 0 0 1-4-4v-2a2 2 0 0 1 2-2h1V6Z'
  },
  c3: {
    title: '肉类零食',
    icon: 'M6 7.5C6 5.567 7.567 4 9.5 4h5C16.433 4 18 5.567 18 7.5v1.2c0 1.3-.63 2.52-1.69 3.25L14.5 13.2V16a4 4 0 0 1-4 4h-1a4 4 0 0 1-4-4v-2.8L6.69 11.95A4.01 4.01 0 0 1 5 8.7V7.5h1Z'
  },
  c4: {
    title: '膨化食品',
    icon: 'M4 9a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v6a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3V9Zm3-1a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h10a1 1 0 0 0 1-1V9a1 1 0 0 0-1-1H7Z'
  },
  c5: {
    title: '饼干糕点',
    icon: 'M6 10a6 6 0 1 1 12 0v4a6 6 0 1 1-12 0v-4Zm6-4a4 4 0 0 0-4 4v4a4 4 0 1 0 8 0v-4a4 4 0 0 0-4-4Z'
  },
  c6: {
    title: '饮品冲调',
    icon: 'M7 4h10l-1 13a3 3 0 0 1-3 3H11a3 3 0 0 1-3-3L7 4Zm2.1 2l.78 10.1c.03.52.47.9 1 .9h2.04c.53 0 .97-.38 1-.9L14.9 6H9.1Z'
  },
  c7: {
    title: '果干蜜饯',
    icon: 'M12 3c4.97 0 9 3.582 9 8 0 4.2-3.44 7.65-7.85 7.98L12 21l-1.15-2.02C6.44 18.65 3 15.2 3 11c0-4.418 4.03-8 9-8Zm0 2c-3.86 0-7 2.686-7 6 0 3.12 2.78 5.67 6.33 5.98l.67.04l.67-.04C16.22 16.67 19 14.12 19 11c0-3.314-3.14-6-7-6Zm-2.5 6a1.5 1.5 0 1 0 0 3a1.5 1.5 0 0 0 0-3Zm5 0a1.5 1.5 0 1 0 0 3a1.5 1.5 0 0 0 0-3Z'
  },
  c8: {
    title: '海苔紫菜',
    icon: 'M6 4h12a2 2 0 0 1 2 2v10a6 6 0 0 1-6 6H10a6 6 0 0 1-6-6V6a2 2 0 0 1 2-2Zm0 2v10a4 4 0 0 0 4 4h4a4 4 0 0 0 4-4V6H6Zm3 2h2v2H9V8Zm4 0h2v2h-2V8Zm-4 4h6v2H9v-2Z'
  },
  c9: {
    title: '速食代餐',
    icon: 'M7 3h10v2H7V3Zm-2 4h14l-1 13a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 7Zm2.17 2L8 20h8l.83-11H7.17ZM9 10h6v2H9v-2Zm0 4h6v2H9v-2Z'
  }
}

const filteredProducts = computed(() => {
  const key = keyword.value.trim()
  return store.state.products.filter((product) => {
    if (product.status !== '上架') return false
    const matchCategory = activeCategory.value === 'all' || product.categoryId === activeCategory.value
    const matchKeyword = !key || product.name.includes(key)
    return matchCategory && matchKeyword
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredProducts.value.length / pageSize)))
const pagedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredProducts.value.slice(start, start + pageSize)
})

watch([activeCategory, keyword], () => {
  currentPage.value = 1
})

watch(totalPages, (next) => {
  if (currentPage.value > next) currentPage.value = next
})

const pageList = computed(() => {
  const maxButtons = 7
  const total = totalPages.value
  const page = currentPage.value
  if (total <= maxButtons) return Array.from({ length: total }, (_, i) => i + 1)
  const list = [1]
  let start = Math.max(2, page - 2)
  let end = Math.min(total - 1, page + 2)
  if (start > 2) list.push('...')
  for (let p = start; p <= end; p++) list.push(p)
  if (end < total - 1) list.push('...')
  list.push(total)
  return list
})

const goPage = async (page) => {
  if (page === '...') return
  const next = Number(page)
  if (!Number.isFinite(next)) return
  currentPage.value = Math.max(1, Math.min(totalPages.value, next))
  await scrollToBrowse()
}

const featuredProducts = ref([])
const newArrivals = ref([])

onMounted(async () => {
  featuredProducts.value = await store.fetchFeaturedProducts(3).catch(() =>
    store.state.products.filter((p) => p.status === '上架').slice(0, 3)
  )
  newArrivals.value = await store.fetchNewArrivals(3).catch(() =>
    store.state.products.filter((p) => p.status === '上架').slice(0, 3)
  )
})

const onAddToCart = async (productId) => {
  if (!currentUser.value) {
    router.push('/login')
    return
  }
  const ok = await store.addToCart(productId)
  if (ok) showToast('已加入购物车')
}

const onSearch = () => {
  const q = keyword.value.trim()
  router.push({ path: '/', query: q ? { q } : {} })
  if (q) scrollToBrowse()
}

const onClickPopular = (q) => {
  keyword.value = q
  router.push({ path: '/', query: { q } })
  scrollToBrowse()
}
</script>

<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <div class="hero-copy">
          <h1 class="hero-title">从今天开始，把零食买得更快乐</h1>
          <p class="hero-subtitle">搜索即是 CTA：先搜口味，再挑爆款。支持购物车、下单、订单管理与售后。</p>
          <form class="hero-search" @submit.prevent="onSearch">
            <input v-model.trim="keyword" class="hero-search-input" placeholder="例如：牛肉干 / 薯片 / 巧克力" />
            <button class="cta" type="submit">立即搜索</button>
          </form>
          <div class="hero-popular">
            <div class="muted">热门搜索</div>
            <div class="inline-list">
              <button
                v-for="q in popularSearches"
                :key="q"
                class="ghost"
                type="button"
                @click="onClickPopular(q)"
              >
                {{ q }}
              </button>
            </div>
          </div>
          <div class="hero-metrics">
            <div class="metric">
              <div class="metric-value">{{ store.state.products.length }}</div>
              <div class="muted">商品</div>
            </div>
            <div class="metric">
              <div class="metric-value">{{ store.state.categories.length }}</div>
              <div class="muted">分类</div>
            </div>
            <div class="metric">
              <div class="metric-value">{{ cartCount }}</div>
              <div class="muted">购物车</div>
            </div>
          </div>
        </div>
        <div class="hero-panel">
          <div class="hero-panel-card">
            <div class="muted">精选推荐</div>
            <div class="scroll-row">
              <button
                v-for="product in featuredProducts"
                :key="product.id"
                class="mini-card"
                type="button"
                @click="router.push(`/product/${product.id}`)"
              >
                <img :src="product.image" :alt="product.name" />
                <div class="mini-card-body">
                  <div class="mini-card-title">{{ product.name }}</div>
                  <div class="mini-card-meta">
                    <span class="tag">{{ product.price.toFixed(2) }} 元</span>
                    <span class="pill">库存 {{ product.stock }}</span>
                  </div>
                </div>
              </button>
            </div>
          </div>
          <div class="hero-panel-card">
            <div class="muted">今日上新</div>
            <div class="scroll-row">
              <button
                v-for="product in newArrivals"
                :key="product.id"
                class="rank-item rank-item-new"
                type="button"
                @click="router.push(`/product/${product.id}`)"
              >
                <div class="rank-title">{{ product.name }}</div>
                <div class="rank-bottom">
                  <img class="rank-thumb" :src="product.image" :alt="product.name" />
                  <div class="rank-meta">
                    <span class="pill">库存 {{ product.stock }}</span>
                    <span class="tag">{{ product.price.toFixed(2) }} 元</span>
                  </div>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section-block">
      <div ref="browseRef" class="browse-layout">
        <aside class="card browse-sidebar">
          <div class="muted">分类</div>
          <div class="category-list">
            <button
              class="category-card category-side"
              :class="{ active: activeCategory === 'all' }"
              type="button"
              @click="activeCategory = 'all'"
            >
              <div class="category-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    fill="currentColor"
                    d="M4 6a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v4a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6Zm10 0a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V6ZM4 16a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-2Zm10-2h4a2 2 0 0 1 2 2v4h-6a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2Z"
                  />
                </svg>
              </div>
              <div class="category-body">
                <div class="category-title">全部</div>
                <div class="muted">浏览所有商品</div>
              </div>
            </button>
            <button
              v-for="category in store.state.categories"
              :key="category.id"
              class="category-card category-side"
              :class="{ active: activeCategory === category.id }"
              type="button"
              @click="activeCategory = category.id"
            >
              <div class="category-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" :d="categoryMeta[category.id]?.icon || categoryMeta.c4.icon" />
                </svg>
              </div>
              <div class="category-body">
                <div class="category-title">{{ category.name }}</div>
                <div class="muted">查看该分类</div>
              </div>
            </button>
          </div>
        </aside>

        <div class="browse-main">
          <div class="section-head">
            <h2 class="section-title">商品列表</h2>
            <div class="muted">支持搜索与分类筛选</div>
          </div>
          <div class="grid">
            <div v-for="product in pagedProducts" :key="product.id" class="card">
              <img :src="product.image" :alt="product.name" />
              <div>
                <div style="font-weight: 800">{{ product.name }}</div>
                <div class="muted">库存 {{ product.stock }}</div>
              </div>
              <div class="inline-list">
                <span class="tag">{{ product.price.toFixed(2) }} 元</span>
                <span class="pill">{{ product.status }}</span>
              </div>
              <div class="inline-list">
                <button class="ghost" type="button" @click="router.push(`/product/${product.id}`)">
                  查看详情
                </button>
                <button
                  class="cta"
                  type="button"
                  :disabled="product.stock === 0 || product.status !== '上架'"
                  @click="onAddToCart(product.id)"
                >
                  加入购物车
                </button>
              </div>
            </div>
          </div>
          <div v-if="!filteredProducts.length" class="muted" style="margin-top: 14px">
            没有匹配的商品，换个关键词试试。
          </div>
          <div v-else-if="totalPages > 1" class="pagination">
            <button class="ghost" type="button" :disabled="currentPage === 1" @click="goPage(currentPage - 1)">
              上一页
            </button>
            <button
              v-for="p in pageList"
              :key="String(p)"
              class="ghost page-btn"
              type="button"
              :class="{ active: p === currentPage }"
              :disabled="p === '...'"
              @click="goPage(p)"
            >
              {{ p }}
            </button>
            <button
              class="ghost"
              type="button"
              :disabled="currentPage === totalPages"
              @click="goPage(currentPage + 1)"
            >
              下一页
            </button>
            <div class="muted">第 {{ currentPage }} / {{ totalPages }} 页</div>
          </div>
        </div>
      </div>
    </section>

    <section class="section-block">
      <div class="section-head">
        <h2 class="section-title">安全与信任</h2>
        <div class="muted">让下单更安心</div>
      </div>
      <div class="trust-grid">
        <div class="trust-card">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path
              fill="currentColor"
              d="M12 2l7 4v6c0 5-3 9-7 10C8 21 5 17 5 12V6l7-4Zm3.6 7.2l-4.3 6.1L8.4 12.7l1.2-1.1l1.7 1.6l3.2-4.6l1.1.6Z"
            />
          </svg>
          <div>
            <div class="trust-title">正品保障</div>
            <div class="muted">严选零食，品牌好货更放心</div>
          </div>
        </div>
        <div class="trust-card">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path
              fill="currentColor"
              d="M3 7h13v10H3V7Zm2 2v6h9V9H5Zm13 1h2v7a3 3 0 0 1-3 3H6v-2h11a1 1 0 0 0 1-1v-7Zm-1 0h2l2 3v2h-4v-5Z"
            />
          </svg>
          <div>
            <div class="trust-title">快速发货</div>
            <div class="muted">后台一键发货，订单状态流转清晰</div>
          </div>
        </div>
        <div class="trust-card">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path
              fill="currentColor"
              d="M12 3a7 7 0 0 1 7 7v2a4 4 0 0 1-4 4h-1v3l-3-3H9a4 4 0 0 1-4-4v-2a7 7 0 0 1 7-7Zm0 2a5 5 0 0 0-5 5v2a2 2 0 0 0 2 2h2.8L14 18.2V14h1a2 2 0 0 0 2-2v-2a5 5 0 0 0-5-5Z"
            />
          </svg>
          <div>
            <div class="trust-title">售后无忧</div>
            <div class="muted">订单详情可取消/确认收货，易展示</div>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-strip">
      <div class="cta-strip-inner">
        <div>
          <div class="cta-strip-title">准备好下单了吗？</div>
          <div class="muted">登录后可加入购物车、提交订单并查看订单状态</div>
        </div>
        <div class="inline-list">
          <button v-if="!currentUser" class="cta" type="button" @click="router.push('/login')">
            去登录
          </button>
          <button class="ghost" type="button" @click="router.push('/cart')">查看购物车</button>
        </div>
      </div>
    </section>
    <div v-if="toastVisible" class="toast">{{ toastText }}</div>
  </div>
</template>
