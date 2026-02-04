<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const route = useRoute()
const router = useRouter()
const store = useShopStore()
const currentUser = store.currentUser

const product = computed(() => store.state.products.find((p) => p.id === route.params.id))
const isOnline = computed(() => product.value?.status === '上架')
watch(
  () => route.params.id,
  (id) => {
    if (id) store.fetchProductDetail(id).catch(() => {})
  },
  { immediate: true }
)
const images = computed(() => {
  if (!product.value) return []
  const list = Array.isArray(product.value.images) && product.value.images.length ? product.value.images : []
  const fallback = product.value.image ? [product.value.image] : []
  return list.length ? list : fallback
})

const activeIndex = ref(0)
const galleryRef = ref(null)
const quantity = ref(1)

const clampQuantity = (next) => {
  const stock = product.value?.stock ?? 0
  const safe = Math.max(1, Math.min(Number(next) || 1, Math.max(1, stock)))
  quantity.value = safe
}

const scrollToIndex = async (index) => {
  const el = galleryRef.value
  if (!el) return
  const total = images.value.length
  if (!total) return
  const nextIndex = Math.max(0, Math.min(index, total - 1))
  activeIndex.value = nextIndex
  await nextTick()
  el.scrollTo({ left: el.clientWidth * nextIndex, behavior: 'smooth' })
}

const onScroll = () => {
  const el = galleryRef.value
  if (!el) return
  const width = el.clientWidth
  if (!width) return
  const idx = Math.round(el.scrollLeft / width)
  if (idx !== activeIndex.value) activeIndex.value = idx
}

let onResize = null
watch(
  () => product.value?.id,
  async () => {
    activeIndex.value = 0
    quantity.value = 1
    await nextTick()
    const el = galleryRef.value
    if (el) el.scrollLeft = 0
  },
  { immediate: true }
)

watch(
  () => images.value.length,
  async () => {
    activeIndex.value = 0
    await nextTick()
    const el = galleryRef.value
    if (el) el.scrollLeft = 0
  }
)

onResize = async () => {
  await scrollToIndex(activeIndex.value)
}
window.addEventListener('resize', onResize)
onBeforeUnmount(() => {
  if (onResize) window.removeEventListener('resize', onResize)
})

const onAddToCart = async () => {
  if (!currentUser.value) {
    router.push('/login')
    return
  }
  if (product.value) {
    await store.addToCart(product.value.id, quantity.value)
    router.push('/cart')
  }
}

const onBuyNow = async () => {
  if (!currentUser.value) {
    router.push('/login')
    return
  }
  if (product.value) {
    const ok = await store.startBuyNow(product.value.id, quantity.value)
    if (ok) router.push('/checkout')
  }
}

const infoRows = computed(() => {
  if (!product.value) return []
  const p = product.value
  return [
    { label: '品牌', value: p.brand || '—' },
    { label: '发货地', value: p.shipFrom || '—' },
    { label: '产地', value: p.origin || '—' },
    { label: 'SKU', value: p.sku || '—' }
  ]
})

const specRows = computed(() => {
  if (!product.value || !product.value.specs) return []
  return Object.entries(product.value.specs).map(([label, value]) => ({ label, value }))
})

const reviewSummary = computed(() =>
  product.value ? store.getProductReviewSummary(product.value.id) : { average: 0, count: 0 }
)
const reviews = computed(() => (product.value ? store.getProductReviews(product.value.id) : []))
const userName = (userId) => store.state.users.find((u) => u.id === userId)?.username || '用户'
</script>

<template>
  <div v-if="product && isOnline">
    <div class="page-title">{{ product.name }}</div>
    <div class="split">
      <div class="gallery">
        <div class="gallery-main">
          <div class="gallery-track" ref="galleryRef" @scroll.passive="onScroll">
            <div v-for="(img, idx) in images" :key="`${product.id}-${idx}`" class="gallery-slide">
              <img :src="img" :alt="product.name" class="gallery-img" />
            </div>
          </div>
          <button
            class="gallery-btn ghost"
            type="button"
            :disabled="activeIndex === 0"
            @click="scrollToIndex(activeIndex - 1)"
          >
            上一张
          </button>
          <button
            class="gallery-btn gallery-btn-right ghost"
            type="button"
            :disabled="activeIndex === images.length - 1"
            @click="scrollToIndex(activeIndex + 1)"
          >
            下一张
          </button>
          <div class="gallery-indicator">
            {{ Math.min(activeIndex + 1, images.length) }} / {{ images.length }}
          </div>
        </div>
        <div v-if="images.length > 1" class="gallery-thumbs">
          <button
            v-for="(img, idx) in images"
            :key="`${product.id}-thumb-${idx}`"
            class="thumb"
            type="button"
            :class="{ active: idx === activeIndex }"
            @click="scrollToIndex(idx)"
          >
            <img :src="img" :alt="product.name" />
          </button>
        </div>
      </div>
      <div class="card">
        <div class="inline-list">
          <span class="tag">{{ product.price.toFixed(2) }} 元</span>
          <span class="pill">{{ product.status }}</span>
        </div>
        <div class="rating-summary">
          <div class="stars" :aria-label="`评分 ${reviewSummary.average} 分`">
            <span
              v-for="n in 5"
              :key="n"
              class="star"
              :class="{ on: n <= Math.round(reviewSummary.average) }"
            >
              ★
            </span>
          </div>
          <a class="muted" href="#reviews">
            {{ reviewSummary.average ? `${reviewSummary.average} 分` : '暂无评分' }}
            <span v-if="reviewSummary.count">（{{ reviewSummary.count }}条评价）</span>
          </a>
        </div>
        <div>库存：{{ product.stock }}</div>
        <div class="muted">{{ product.description }}</div>
        <div class="qty-row">
          <div class="muted">购买数量</div>
          <div class="qty">
            <button class="ghost qty-btn" type="button" :disabled="quantity <= 1" @click="clampQuantity(quantity - 1)">
              -
            </button>
            <input
              class="qty-input"
              type="number"
              min="1"
              :max="product.stock"
              :value="quantity"
              @input="(e) => clampQuantity(e.target.value)"
            />
            <button
              class="ghost qty-btn"
              type="button"
              :disabled="quantity >= product.stock"
              @click="clampQuantity(quantity + 1)"
            >
              +
            </button>
          </div>
        </div>
        <div class="inline-list">
          <button
            class="cta"
            :disabled="product.stock === 0 || product.status !== '上架'"
            @click="onAddToCart"
          >
            加入购物车
          </button>
          <button
            :disabled="product.stock === 0 || product.status !== '上架'"
            @click="onBuyNow"
          >
            立即下单
          </button>
          <button class="ghost" @click="router.push('/')">返回列表</button>
        </div>
      </div>
    </div>
    <div class="detail-grid">
      <div class="card">
        <div class="section-head">
          <h2 class="section-title">商品信息</h2>
          <div class="muted">更贴近真实零食商场展示</div>
        </div>
        <div class="kv-grid">
          <div v-for="row in infoRows" :key="row.label" class="kv">
            <div class="muted">{{ row.label }}</div>
            <div class="kv-value">{{ row.value }}</div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="section-head">
          <h2 class="section-title">规格参数</h2>
          <div class="muted">口味/净含量/保质期等</div>
        </div>
        <div v-if="specRows.length" class="spec-grid">
          <div v-for="row in specRows" :key="row.label" class="spec-row">
            <div class="muted">{{ row.label }}</div>
            <div class="kv-value">{{ row.value }}</div>
          </div>
        </div>
        <div v-else class="muted">暂无规格信息</div>
      </div>
    </div>
    <div id="reviews" class="card section">
      <div class="section-head">
        <h2 class="section-title">商品评价</h2>
        <div class="muted">
          {{ reviewSummary.count ? `平均 ${reviewSummary.average} 分 · ${reviewSummary.count} 条` : '暂无评价' }}
        </div>
      </div>

      <div v-if="reviews.length" class="review-list">
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-meta">
            <div class="review-user">{{ userName(review.userId) }}</div>
            <div class="stars" :aria-label="`评分 ${review.rating} 分`">
              <span v-for="n in 5" :key="n" class="star" :class="{ on: n <= review.rating }">★</span>
            </div>
            <div class="muted">{{ review.createdAt }}</div>
          </div>
          <div class="review-content">{{ review.content }}</div>
        </div>
      </div>
      <div v-else class="muted">还没有人评价，欢迎抢沙发。</div>
    </div>
  </div>
  <div v-else class="card">
    <div class="page-title">商品不存在或已下架</div>
    <div class="muted">请返回首页浏览其他商品</div>
    <div style="margin-top: 12px">
      <button class="cta" type="button" @click="router.push('/')">返回首页</button>
    </div>
  </div>
</template>
