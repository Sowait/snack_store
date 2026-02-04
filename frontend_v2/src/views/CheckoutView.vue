<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const currentUser = store.currentUser
const cartItems = store.cartItems
const cartTotal = store.cartTotal

const paymentMethod = ref('微信支付')
const selectedAddressId = ref('')
const addresses = store.currentUserAddresses

watch(
  addresses,
  (list) => {
    if (!selectedAddressId.value && list.length) {
      const defaultAddr = list.find((addr) => addr.isDefault) || list[0]
      selectedAddressId.value = defaultAddr.id
    }
  },
  { immediate: true }
)

const onSubmit = async () => {
  if (!currentUser.value) {
    router.push('/login')
    return
  }
  const order = await store.createOrder({
    addressId: selectedAddressId.value,
    paymentMethod: paymentMethod.value
  })
  if (order) {
    router.push(`/orders/${order.id}`)
  }
}
</script>

<template>
  <div>
    <div class="page-title">提交订单</div>
    <div v-if="cartItems.length" class="split">
      <div class="card">
        <div class="section">
          <div class="muted">选择收货地址</div>
          <div class="form">
            <select v-model="selectedAddressId">
              <option v-for="addr in addresses" :key="addr.id" :value="addr.id">
                {{ addr.region }} {{ addr.detail }}（{{ addr.name }}）
              </option>
            </select>
            <div class="muted" v-if="!addresses.length">请先在个人中心新增地址</div>
          </div>
        </div>
        <div class="section">
          <div class="muted">支付方式</div>
          <div class="inline-list">
            <label class="pill">
              <input type="radio" v-model="paymentMethod" value="微信支付" />
              微信支付
            </label>
            <label class="pill">
              <input type="radio" v-model="paymentMethod" value="支付宝" />
              支付宝
            </label>
            <label class="pill">
              <input type="radio" v-model="paymentMethod" value="银行卡" />
              银行卡
            </label>
          </div>
        </div>
        <button class="cta" :disabled="!selectedAddressId" @click="onSubmit">确认提交</button>
      </div>
      <div class="card">
        <div class="muted">订单明细</div>
        <div v-for="item in cartItems" :key="item.product.id" class="toolbar">
          <div>{{ item.product.name }}</div>
          <div class="muted">x{{ item.quantity }}</div>
          <div class="tag">{{ item.subtotal.toFixed(2) }} 元</div>
        </div>
        <div class="toolbar">
          <div class="tag">合计 {{ cartTotal.toFixed(2) }} 元</div>
        </div>
      </div>
    </div>
    <div v-else class="muted">购物车为空</div>
  </div>
</template>
