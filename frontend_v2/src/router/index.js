import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import CartView from '../views/CartView.vue'
import CheckoutView from '../views/CheckoutView.vue'
import OrdersView from '../views/OrdersView.vue'
import OrderDetailView from '../views/OrderDetailView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import AccountView from '../views/AccountView.vue'
import AdminLoginView from '../views/admin/AdminLoginView.vue'
import AdminProductsView from '../views/admin/AdminProductsView.vue'
import AdminOrdersView from '../views/admin/AdminOrdersView.vue'
import AdminUsersView from '../views/admin/AdminUsersView.vue'
import { useShopStore } from '../store/shopStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/product/:id', name: 'product', component: ProductDetailView },
    { path: '/cart', name: 'cart', component: CartView },
    { path: '/checkout', name: 'checkout', component: CheckoutView },
    { path: '/orders', name: 'orders', component: OrdersView },
    { path: '/orders/:id', name: 'order-detail', component: OrderDetailView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/account', name: 'account', component: AccountView },
    { path: '/admin/login', name: 'admin-login', component: AdminLoginView },
    { path: '/admin/products', name: 'admin-products', component: AdminProductsView },
    { path: '/admin/orders', name: 'admin-orders', component: AdminOrdersView },
    { path: '/admin/users', name: 'admin-users', component: AdminUsersView }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  if (!to.path.startsWith('/admin')) return true

  const store = useShopStore()
  await store.bootstrapDone

  const isAdminLoggedIn = Boolean(store.currentAdmin.value)
  const isAdminLoginPage = to.path === '/admin/login'

  if (!isAdminLoggedIn && !isAdminLoginPage) return { path: '/admin/login' }
  if (isAdminLoggedIn && isAdminLoginPage) return { path: '/admin/products' }

  return true
})

export default router
