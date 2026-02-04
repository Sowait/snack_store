<script setup>
import { ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const route = useRoute()
const currentUser = store.currentUser

const search = ref(typeof route.query.q === 'string' ? route.query.q : '')

watch(
  () => route.query.q,
  (next) => {
    search.value = typeof next === 'string' ? next : ''
  }
)

const onSearch = () => {
  const q = search.value.trim()
  router.push({ path: '/', query: q ? { q } : {} })
}
</script>

<template>
  <div class="front">
    <a class="skip-link" href="#main">跳到主要内容</a>
    <header class="front-nav">
      <div class="container nav-inner">
        <RouterLink class="logo" to="/">零食商场</RouterLink>
        <form class="nav-search" @submit.prevent="onSearch">
          <input v-model.trim="search" class="nav-search-input" placeholder="搜索零食、品牌、口味" />
          <button class="cta" type="submit">搜索</button>
        </form>
        <nav class="nav-links">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/cart">购物车</RouterLink>
          <RouterLink to="/orders">订单</RouterLink>
          <RouterLink to="/account">个人中心</RouterLink>
          <RouterLink to="/admin/products">管理后台</RouterLink>
        </nav>
        <div class="nav-actions">
          <template v-if="currentUser">
            <RouterLink class="user-chip" to="/account">
              <img
                v-if="currentUser.avatar"
                :src="currentUser.avatar"
                alt="avatar"
                class="avatar"
              />
              <div>{{ currentUser.username }}</div>
            </RouterLink>
          </template>
          <template v-else>
            <RouterLink class="nav-auth" to="/login">登录</RouterLink>
            <RouterLink class="nav-auth nav-auth-strong" to="/register">注册</RouterLink>
          </template>
        </div>
      </div>
    </header>
    <main id="main" class="container front-main">
      <RouterView />
    </main>
    <footer class="front-footer">
      <div class="container footer-inner">
        <div class="muted">零食商场 · 正品零食直达</div>
        <div class="muted">用心挑选好零食，支持售后</div>
      </div>
    </footer>
  </div>
</template>
