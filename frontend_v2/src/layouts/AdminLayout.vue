<script setup>
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const route = useRoute()
const currentAdmin = store.currentAdmin

const onAdminLogout = () => {
  store.logoutAdmin()
  if (route.path.startsWith('/admin')) {
    router.push('/admin/login')
  }
}
</script>

<template>
  <div class="admin">
    <template v-if="currentAdmin">
      <header class="admin-topbar">
        <div class="container admin-topbar-inner">
          <RouterLink class="admin-brand" to="/">零食商场后台</RouterLink>
          <div class="admin-actions">
            <RouterLink class="admin-link" to="/admin/products">商品</RouterLink>
            <RouterLink class="admin-link" to="/admin/orders">订单</RouterLink>
            <RouterLink class="admin-link" to="/admin/users">用户</RouterLink>
            <RouterLink class="admin-link" to="/">返回前台</RouterLink>
            <button class="ghost" type="button" @click="onAdminLogout">退出</button>
          </div>
        </div>
      </header>
      <div class="container admin-shell">
        <aside class="admin-sidebar">
          <div class="menu-title">核心模块</div>
          <RouterLink to="/admin/products">商品管理</RouterLink>
          <RouterLink to="/admin/orders">订单管理</RouterLink>
          <RouterLink to="/admin/users">用户管理</RouterLink>
        </aside>
        <main class="admin-main">
          <RouterView />
        </main>
      </div>
    </template>
    <template v-else>
      <div class="container">
        <RouterView />
      </div>
    </template>
  </div>
</template>
