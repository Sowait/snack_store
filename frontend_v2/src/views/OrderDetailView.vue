<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const route = useRoute()
const router = useRouter()
const currentUser = store.currentUser

const order = computed(() => store.state.orders.find((o) => o.id === route.params.id))
const productsById = computed(() =>
  store.state.products.reduce((map, p) => {
    map[p.id] = p
    return map
  }, {})
)

const isReviewed = (productId) => {
  const uid = currentUser.value?.id
  if (!uid) return false
  return store.state.reviews.some((r) => r.productId === productId && r.userId === uid)
}

const reviewProductId = ref('')
const reviewRating = ref(5)
const reviewContent = ref('')

const openReview = (productId) => {
  reviewProductId.value = productId
  reviewRating.value = 5
  reviewContent.value = ''
}

const closeReview = () => {
  reviewProductId.value = ''
  reviewRating.value = 5
  reviewContent.value = ''
}

const setRating = (value) => {
  const next = Number(value)
  if (!Number.isFinite(next)) return
  reviewRating.value = Math.max(1, Math.min(5, next))
}

const onSubmitReview = (productId) => {
  const ok = store.addProductReview(productId, { rating: reviewRating.value, content: reviewContent.value })
  if (ok) {
    closeReview()
  }
}

const statusClass = (status) => {
  if (status === '待发货' || status === '待收货') return 'status wait'
  if (status === '已完成') return 'status success'
  if (status === '已取消') return 'status cancel'
  return 'status'
}

const onCancel = () => {
  if (order.value) {
    store.updateOrderStatus(order.value.id, '已取消')
  }
}

const onConfirm = () => {
  if (order.value) {
    store.updateOrderStatus(order.value.id, '已完成')
  }
}
</script>

<template>
  <div v-if="order">
    <div class="page-title">订单详情</div>
    <div class="card section">
      <div class="toolbar">
        <div>订单号：{{ order.id }}</div>
        <span :class="statusClass(order.status)">{{ order.status }}</span>
      </div>
      <div class="muted">下单时间：{{ order.createdAt }}</div>
    </div>
    <div class="split">
      <div class="card">
        <div class="muted">收货信息</div>
        <div>{{ order.address.name }} {{ order.address.phone }}</div>
        <div>{{ order.address.region }} {{ order.address.detail }}</div>
      </div>
      <div class="card">
        <div class="muted">订单金额</div>
        <div class="tag">{{ order.amount.toFixed(2) }} 元</div>
      </div>
    </div>
    <div class="card section">
      <div class="muted">商品明细</div>
      <div v-for="item in order.items" :key="item.productId" class="order-item">
        <div class="toolbar">
          <div class="inline-list">
            <img
              v-if="item.productImage || productsById[item.productId]?.image"
              :src="item.productImage || productsById[item.productId]?.image"
              alt="product"
              style="width: 44px; height: 44px; border-radius: 12px; object-fit: cover"
            />
            <div class="order-item-name" @click="router.push(`/product/${item.productId}`)">
              {{ productsById[item.productId]?.name || item.productName || '商品' }}
            </div>
          </div>
          <div class="muted">x{{ item.quantity }}</div>
          <div class="tag">{{ item.price.toFixed(2) }} 元</div>
          <button
            v-if="order.status === '已完成' && store.canReviewProduct(item.productId)"
            class="ghost"
            type="button"
            @click="openReview(item.productId)"
          >
            写评价
          </button>
          <div v-else-if="order.status === '已完成' && isReviewed(item.productId)" class="stamp">
            已评价
          </div>
        </div>

        <div v-if="reviewProductId === item.productId" class="review-form">
          <div class="muted">评价该商品</div>
          <div class="review-form-inner">
            <div class="rating-picker" role="radiogroup" aria-label="选择评分">
              <button
                v-for="n in 5"
                :key="n"
                type="button"
                class="star-btn"
                :class="{ on: n <= reviewRating }"
                @click="setRating(n)"
              >
                ★
              </button>
              <div class="muted">{{ reviewRating }} 分</div>
            </div>
            <textarea
              v-model.trim="reviewContent"
              placeholder="说说口味、包装、发货体验等（最多240字）"
              rows="3"
            />
            <div class="inline-list">
              <button class="cta" type="button" :disabled="!reviewContent" @click="onSubmitReview(item.productId)">
                提交评价
              </button>
              <button class="ghost" type="button" @click="closeReview">取消</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="toolbar">
      <button class="ghost" @click="router.push('/orders')">返回订单列表</button>
      <button v-if="order.status === '待支付'" class="ghost" @click="onCancel">取消订单</button>
      <button v-if="order.status === '待收货'" class="cta" @click="onConfirm">确认收货</button>
    </div>
  </div>
  <div v-else class="muted">订单不存在</div>
</template>
