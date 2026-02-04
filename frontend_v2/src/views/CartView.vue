<script setup>
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const currentUser = store.currentUser
const cartItems = store.cartItems
const cartCount = store.cartCount
const cartTotal = store.cartTotal

const safeQuantity = (event, productId) => {
  const value = Number(event.target.value)
  if (Number.isFinite(value) && value > 0) {
    store.updateCartQuantity(productId, value)
  }
}

const goCheckout = () => {
  if (!currentUser.value) {
    router.push('/login')
    return
  }
  router.push('/checkout')
}
</script>

<template>
  <div>
    <div class="page-title">购物车</div>
    <div v-if="cartItems.length" class="section">
      <table class="table">
        <thead>
          <tr>
            <th>商品</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartItems" :key="item.product.id">
            <td>
              <div class="cart-product">
                <img class="cart-thumb" :src="item.product.image" :alt="item.product.name" />
                <div>
                  <div class="cart-name" @click="router.push(`/product/${item.product.id}`)">
                    {{ item.product.name }}
                  </div>
                  <div class="muted">库存 {{ item.product.stock }}</div>
                </div>
              </div>
            </td>
            <td>{{ item.product.price.toFixed(2) }}</td>
            <td>
              <input
                type="number"
                min="1"
                :max="item.product.stock"
                :value="item.quantity"
                @input="(e) => safeQuantity(e, item.product.id)"
              />
            </td>
            <td>{{ item.subtotal.toFixed(2) }}</td>
            <td>
              <button class="ghost" @click="store.removeFromCart(item.product.id)">移除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="muted">购物车为空，去首页挑选零食吧。</div>
    <div v-if="cartItems.length" class="toolbar">
      <div class="muted">共 {{ cartCount }} 件</div>
      <div class="tag">合计 {{ cartTotal.toFixed(2) }} 元</div>
      <button class="ghost" @click="store.clearCart">清空购物车</button>
      <button class="cta" @click="goCheckout">去结算</button>
    </div>
  </div>
</template>
