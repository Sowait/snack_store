<script setup>
import { computed, ref, watch } from 'vue'
import { useShopStore } from '../../store/shopStore'

const store = useShopStore()
const currentAdmin = store.currentAdmin

const productsById = computed(() =>
  store.state.products.reduce((map, p) => {
    map[p.id] = p
    return map
  }, {})
)

const pageSize = ref(10)
const page = ref(1)
const total = computed(() => store.state.orders.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const pagedOrders = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return store.state.orders.slice(start, start + pageSize.value)
})

watch(pageSize, () => {
  page.value = 1
})

watch([total, totalPages], () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

const expanded = ref({})

const isExpanded = (orderId) => Boolean(expanded.value[String(orderId)])

const toggleExpanded = (orderId) => {
  const id = String(orderId)
  expanded.value = { ...expanded.value, [id]: !expanded.value[id] }
}

const resolveItemImage = (item) => {
  if (item?.productImage) return item.productImage
  return productsById.value[item?.productId]?.image || ''
}

const resolveItemName = (item) => {
  if (item?.productName) return item.productName
  return productsById.value[item?.productId]?.name || '商品'
}

const statusClass = (status) => {
  if (status === '待发货' || status === '待收货') return 'status wait'
  if (status === '已完成') return 'status success'
  if (status === '已取消') return 'status cancel'
  return 'status'
}
</script>

<template>
  <div>
    <div class="page-title">订单管理</div>
    <div v-if="!currentAdmin" class="muted">请先在后台登录</div>
    <div v-else>
      <table class="table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>下单时间</th>
            <th>用户</th>
            <th>金额</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="order in pagedOrders" :key="order.id">
            <tr>
              <td>{{ order.orderNo || order.id }}</td>
            <td>{{ order.createdAt }}</td>
            <td>
              <div style="font-weight: 700">
                {{ store.state.users.find((u) => u.id === order.userId)?.username || '-' }}
              </div>
              <div class="muted">{{ order.userId }}</div>
            </td>
            <td>{{ order.amount.toFixed(2) }}</td>
            <td><span :class="statusClass(order.status)">{{ order.status }}</span></td>
            <td class="inline-list">
              <button
                v-if="order.status === '待发货'"
                class="ghost"
                @click="store.updateOrderStatus(order.id, '待收货')"
              >
                发货
              </button>
              <button
                v-if="order.status === '待收货'"
                class="ghost"
                @click="store.updateOrderStatus(order.id, '已完成')"
              >
                完成
              </button>
              <button
                v-if="order.status === '待支付' || order.status === '待发货'"
                class="ghost"
                @click="store.updateOrderStatus(order.id, '已取消')"
              >
                取消
              </button>
            </td>
            </tr>
            <tr>
              <td :colspan="6" style="padding: 0">
                <button
                  class="order-expand-toggle"
                  type="button"
                  :aria-label="isExpanded(order.id) ? '收起订单商品明细' : '展开订单商品明细'"
                  :aria-expanded="isExpanded(order.id)"
                  @click="toggleExpanded(order.id)"
                >
                  <span class="order-expand-icon" :class="{ open: isExpanded(order.id) }">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <path
                        d="M7 10l5 5 5-5"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                  </span>
                </button>
              </td>
            </tr>
            <tr v-if="isExpanded(order.id)">
              <td :colspan="6" style="background: rgba(255, 255, 255, 0.6)">
                <div class="card" style="margin: 10px 0">
                  <div class="muted" style="margin-bottom: 10px">商品明细</div>
                  <table class="table" style="margin: 0">
                    <thead>
                      <tr>
                        <th>商品</th>
                        <th style="width: 100px">单价</th>
                        <th style="width: 100px">数量</th>
                        <th style="width: 120px">小计</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="item in order.items" :key="`${order.id}-${item.productId}`">
                        <td>
                          <div class="inline-list">
                            <img
                              v-if="resolveItemImage(item)"
                              :src="resolveItemImage(item)"
                              alt="product"
                              style="width: 40px; height: 40px; border-radius: 10px; object-fit: cover"
                            />
                            <div>
                              <div style="font-weight: 800">{{ resolveItemName(item) }}</div>
                              <div class="muted">{{ item.productId }}</div>
                            </div>
                          </div>
                        </td>
                        <td>{{ Number(item.price || 0).toFixed(2) }}</td>
                        <td>{{ item.quantity }}</td>
                        <td>{{ (Number(item.price || 0) * Number(item.quantity || 0)).toFixed(2) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </td>
            </tr>
          </template>
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
    </div>
  </div>
</template>
