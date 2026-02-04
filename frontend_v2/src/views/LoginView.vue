<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const phone = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const onLogin = async () => {
  if (!phone.value.trim() || !password.value) return
  loading.value = true
  errorMessage.value = ''
  const ok = await store
    .loginUser(phone.value.trim(), password.value)
    .then((v) => Boolean(v))
    .catch((e) => {
      errorMessage.value = e?.message || '登录失败'
      return false
    })
    .finally(() => {
      loading.value = false
    })
  if (ok) router.push('/')
}
</script>

<template>
  <div>
    <div class="page-title">账号登录</div>
    <div class="split">
      <div class="card">
        <div style="font-weight: 800; font-size: 18px">欢迎回到零食商场</div>
        <div class="muted">登录后可查看订单、管理收货地址、享受更快的结算体验</div>
        <div class="section muted">
          为保障账号安全，请勿在公共设备勾选记住密码或保存登录信息。
        </div>
        <div class="inline-list">
          <RouterLink class="ghost" to="/register">没有账号？去注册</RouterLink>
          <RouterLink class="ghost" to="/">返回首页</RouterLink>
        </div>
      </div>
      <div class="card form">
        <input v-model.trim="phone" placeholder="手机号" autocomplete="tel" />
        <input v-model="password" type="password" placeholder="密码" autocomplete="current-password" />
        <button class="cta" :disabled="loading" @click="onLogin">{{ loading ? '登录中...' : '登录' }}</button>
        <div v-if="errorMessage" class="muted">{{ errorMessage }}</div>
      </div>
    </div>
  </div>
</template>
