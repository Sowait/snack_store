<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useShopStore } from '../../store/shopStore'

const store = useShopStore()
const router = useRouter()

const username = ref('admin')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const onLogin = async () => {
  if (loading.value) return
  errorMessage.value = ''
  if (!username.value.trim() || !password.value) return

  loading.value = true
  const ok = await store
    .loginAdmin(username.value.trim(), password.value)
    .then((v) => Boolean(v))
    .catch((e) => {
      errorMessage.value = e?.message || '登录失败'
      return false
    })
    .finally(() => {
      loading.value = false
    })
  if (ok) router.push('/admin/products')
}
</script>

<template>
  <div class="admin-login">
    <div class="admin-login-top">
      <div>
        <div class="admin-login-brand">零食商场后台</div>
        <div class="muted">管理员登录</div>
      </div>
      <RouterLink class="admin-login-back" to="/">返回前台</RouterLink>
    </div>

    <div class="card admin-login-card">
      <div class="page-title" style="margin-bottom: 8px">后台登录</div>
      <div class="muted">请输入管理员账号与密码</div>
      <div class="form" style="margin-top: 12px">
        <input v-model.trim="username" placeholder="管理员账号" autocomplete="username" />
        <input
          v-model="password"
          type="password"
          placeholder="密码"
          autocomplete="current-password"
          @keyup.enter="onLogin"
        />
        <div v-if="errorMessage" class="form-error">{{ errorMessage }}</div>
        <button class="cta" :disabled="loading" type="button" @click="onLogin">
          {{ loading ? '登录中...' : '登录后台' }}
        </button>
      </div>
    </div>
  </div>
</template>
