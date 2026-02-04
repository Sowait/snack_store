<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useShopStore } from '../../store/shopStore'

const store = useShopStore()
const currentAdmin = store.currentAdmin

const categories = computed(() => store.state.categories)
const categoryName = (id) => categories.value.find((c) => c.id === id)?.name || ''

const pageSize = ref(10)
const page = ref(1)
const total = computed(() => store.state.products.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const pagedProducts = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return store.state.products.slice(start, start + pageSize.value)
})

watch(pageSize, () => {
  page.value = 1
})

watch([total, totalPages], () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

const modalOpen = ref(false)
const modalMode = ref('create')

const productForm = reactive({
  id: '',
  name: '',
  price: '',
  stock: '',
  categoryId: '',
  status: '上架',
  brand: '',
  shipFrom: '',
  origin: '',
  sku: '',
  description: '',
  specNet: '',
  specFlavor: '',
  specShelfLife: '',
  specStorage: '',
  images: ['', '', '', '']
})

const resetForm = () => {
  productForm.id = ''
  productForm.name = ''
  productForm.price = ''
  productForm.stock = ''
  productForm.categoryId = categories.value[0]?.id || ''
  productForm.status = '上架'
  productForm.brand = ''
  productForm.shipFrom = ''
  productForm.origin = ''
  productForm.sku = ''
  productForm.description = ''
  productForm.specNet = ''
  productForm.specFlavor = ''
  productForm.specShelfLife = ''
  productForm.specStorage = ''
  productForm.images = ['', '', '', '']
}

const openCreate = () => {
  resetForm()
  modalMode.value = 'create'
  modalOpen.value = true
}

const openEdit = (product) => {
  resetForm()
  modalMode.value = 'edit'
  productForm.id = product.id
  productForm.name = product.name || ''
  productForm.price = String(product.price ?? '')
  productForm.stock = String(product.stock ?? '')
  productForm.categoryId = product.categoryId || (categories.value[0]?.id || '')
  productForm.status = product.status || '上架'
  productForm.brand = product.brand || ''
  productForm.shipFrom = product.shipFrom || ''
  productForm.origin = product.origin || ''
  productForm.sku = product.sku || ''
  productForm.description = product.description || ''
  const specs = product.specs || {}
  productForm.specNet = specs['净含量'] || ''
  productForm.specFlavor = specs['口味'] || ''
  productForm.specShelfLife = specs['保质期'] || ''
  productForm.specStorage = specs['储存方式'] || ''
  const imgs = Array.isArray(product.images) && product.images.length ? product.images : product.image ? [product.image] : []
  productForm.images = [imgs[0] || '', imgs[1] || '', imgs[2] || '', imgs[3] || '']
  modalOpen.value = true
}

const closeModal = () => {
  modalOpen.value = false
}

const readAsDataUrl = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(String(reader.result || ''))
    reader.onerror = () => reject(new Error('read file error'))
    reader.readAsDataURL(file)
  })

const onUpload = (event, index) => {
  const file = event.target.files?.[0]
  if (!file) return
  readAsDataUrl(file)
    .then((dataUrl) => {
      if (!dataUrl) return
      const next = [...productForm.images]
      next[index] = dataUrl
      productForm.images = next
    })
    .catch(() => {})
    .finally(() => {
      if (event?.target) event.target.value = ''
    })
}

const removeImage = (index) => {
  const next = [...productForm.images]
  next[index] = ''
  productForm.images = next
}

const buildPayload = () => {
  const images = productForm.images.filter(Boolean).slice(0, 4)
  const specs = {}
  if (productForm.specNet) specs['净含量'] = productForm.specNet
  if (productForm.specFlavor) specs['口味'] = productForm.specFlavor
  if (productForm.specShelfLife) specs['保质期'] = productForm.specShelfLife
  if (productForm.specStorage) specs['储存方式'] = productForm.specStorage
  return {
    name: productForm.name,
    price: productForm.price,
    stock: productForm.stock,
    categoryId: productForm.categoryId,
    status: productForm.status,
    brand: productForm.brand,
    shipFrom: productForm.shipFrom,
    origin: productForm.origin,
    sku: productForm.sku,
    description: productForm.description,
    images,
    specs
  }
}

const onSubmit = () => {
  if (!productForm.name) return
  const payload = buildPayload()
  if (modalMode.value === 'create') {
    store.addProduct(payload)
    closeModal()
    return
  }
  if (!productForm.id) return
  store.updateProduct(productForm.id, payload)
  closeModal()
}
</script>

<template>
  <div>
    <div class="page-title">商品管理</div>
    <div v-if="!currentAdmin" class="muted">请先在后台登录</div>
    <div v-else>
      <div class="toolbar">
        <button class="cta" type="button" @click="openCreate">新增商品</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>商品</th>
            <th>品牌</th>
            <th>发货地</th>
            <th>分类</th>
            <th>价格</th>
            <th>库存</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in pagedProducts" :key="product.id">
            <td>
              <div class="admin-product">
                <img v-if="product.image" class="admin-thumb" :src="product.image" :alt="product.name" />
                <div>
                  <div style="font-weight: 700">{{ product.name }}</div>
                  <div class="muted">{{ product.id }}</div>
                </div>
              </div>
            </td>
            <td>{{ product.brand || '-' }}</td>
            <td>{{ product.shipFrom || '-' }}</td>
            <td>{{ categoryName(product.categoryId) }}</td>
            <td>{{ product.price.toFixed(2) }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.status }}</td>
            <td>
              <button class="ghost" type="button" @click="openEdit(product)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="toolbar" style="margin-top: 12px">
        <div class="inline-list" style="margin-left: auto">
          <div class="muted">共 {{ total }} 条</div>
          <select v-model.number="pageSize">
            <option :value="5">5 / 页</option>
            <option :value="10">10 / 页</option>
            <option :value="20">20 / 页</option>
          </select>
          <button class="ghost" type="button" :disabled="page <= 1" @click="page--">上一页</button>
          <div class="muted">{{ page }} / {{ totalPages }}</div>
          <button class="ghost" type="button" :disabled="page >= totalPages" @click="page++">下一页</button>
        </div>
      </div>

      <div v-if="modalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal">
          <div class="modal-head">
            <div style="font-weight: 900">
              {{ modalMode === 'create' ? '新增商品' : '编辑商品' }}
            </div>
            <button class="ghost" type="button" @click="closeModal">关闭</button>
          </div>
          <div class="modal-body">
            <div class="form">
              <div class="grid-2">
                <input v-model="productForm.name" placeholder="商品名称" />
                <input v-model="productForm.brand" placeholder="品牌" />
              </div>
              <div class="grid-2">
                <input v-model="productForm.shipFrom" placeholder="发货地（例如：广东·深圳）" />
                <input v-model="productForm.origin" placeholder="产地（例如：福建·泉州）" />
              </div>
              <div class="grid-2">
                <input v-model="productForm.sku" placeholder="SKU" />
                <select v-model="productForm.categoryId">
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
              </div>
              <div class="grid-2">
                <input v-model="productForm.price" type="number" placeholder="价格" />
                <input v-model="productForm.stock" type="number" placeholder="库存" />
              </div>
              <select v-model="productForm.status">
                <option value="上架">上架</option>
                <option value="下架">下架</option>
              </select>

              <div class="muted">规格参数</div>
              <div class="grid-2">
                <input v-model="productForm.specNet" placeholder="净含量（例如：200g）" />
                <input v-model="productForm.specFlavor" placeholder="口味（例如：原味/香辣）" />
              </div>
              <div class="grid-2">
                <input v-model="productForm.specShelfLife" placeholder="保质期（例如：12个月）" />
                <input v-model="productForm.specStorage" placeholder="储存方式" />
              </div>

              <textarea v-model="productForm.description" rows="3" placeholder="商品描述"></textarea>

              <div class="muted">商品图片（最多4张）</div>
              <div class="img-grid">
                <div v-for="i in 4" :key="i" class="img-slot">
                  <img v-if="productForm.images[i - 1]" :src="productForm.images[i - 1]" class="img-preview" />
                  <div v-else class="img-empty muted">第 {{ i }} 张</div>
                  <div class="inline-list">
                    <input type="file" accept="image/*" @change="(e) => onUpload(e, i - 1)" />
                    <button
                      v-if="productForm.images[i - 1]"
                      class="ghost"
                      type="button"
                      @click="removeImage(i - 1)"
                    >
                      移除
                    </button>
                  </div>
                </div>
              </div>
              <div class="toolbar">
                <button class="ghost" type="button" @click="closeModal">取消</button>
                <button class="cta" type="button" @click="onSubmit">
                  {{ modalMode === 'create' ? '创建商品' : '保存修改' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
