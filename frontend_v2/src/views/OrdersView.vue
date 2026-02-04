<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const statusFilter = ref('全部')
const currentUser = store.currentUser

const productsById = computed(() =>
  store.state.products.reduce((map, p) => {
    map[p.id] = p
    return map
  }, {})
)

const filteredOrders = computed(() => {
  const list = store.currentUserOrders.value
  if (statusFilter.value === '全部') return list
  return list.filter((order) => order.status === statusFilter.value)
})

const getOrderPreview = (order) => {
  const items = Array.isArray(order?.items) ? order.items : []
  if (!items.length) return { title: '商品', sub: '共0件', image: '' }
  const first = items[0]
  const product = productsById.value[first.productId] || null
  const title = product?.name || first.productName || '商品'
  const totalQty = items.reduce((sum, it) => sum + (Number(it?.quantity) || 0), 0)
  const sub = items.length > 1 ? `${title} 等${items.length}件 · 共${totalQty}件` : `共${totalQty}件`
  const image = first.productImage || product?.image || ''
  return { title, sub, image }
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
    <div v-if="!currentUser" class="muted">请先登录查看订单</div>
    <div v-else>
      <div class="toolbar">
        <select v-model="statusFilter">
          <option value="全部">全部状态</option>
          <option value="待支付">待支付</option>
          <option value="待发货">待发货</option>
          <option value="待收货">待收货</option>
          <option value="已完成">已完成</option>
          <option value="已取消">已取消</option>
        </select>
      </div>
      <div v-if="filteredOrders.length">
        <table class="table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>下单时间</th>
              <th>商品</th>
              <th>金额</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in filteredOrders" :key="order.id">
              <td>{{ order.id }}</td>
              <td>{{ order.createdAt }}</td>
              <td>
                <div class="inline-list">
                  <img
                    v-if="getOrderPreview(order).image"
                    :src="getOrderPreview(order).image"
                    alt="product"
                    style="width: 40px; height: 40px; border-radius: 10px; object-fit: cover"
                  />
                  <div>
                    <div style="font-weight: 700">{{ getOrderPreview(order).title }}</div>
                    <div class="muted">{{ getOrderPreview(order).sub }}</div>
                  </div>
                </div>
              </td>
              <td>{{ order.amount.toFixed(2) }}</td>
              <td><span :class="statusClass(order.status)">{{ order.status }}</span></td>
              <td>
                <button class="ghost" @click="router.push(`/orders/${order.id}`)">查看</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="muted">暂无订单</div>
    </div>
  </div>
</template>
