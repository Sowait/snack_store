<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  phone: '',
  email: '',
  password: '',
  avatar: ''
})

const onUpload = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    form.avatar = URL.createObjectURL(file)
  }
}

const onSubmit = () => {
  if (!form.username || !form.phone || !form.password) return
  loading.value = true
  errorMessage.value = ''
  store
    .registerUser({ ...form })
    .then((u) => {
      if (u) router.push('/')
      else errorMessage.value = '注册失败'
    })
    .catch((e) => {
      errorMessage.value = e?.message || '注册失败'
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div>
    <div class="page-title">用户注册</div>
    <div class="card form">
      <input v-model="form.username" placeholder="用户名" />
      <input v-model="form.phone" placeholder="手机号" />
      <input v-model="form.email" placeholder="邮箱（可选）" />
      <input v-model="form.password" type="password" placeholder="密码" autocomplete="new-password" />
      <input type="file" accept="image/*" @change="onUpload" />
      <div v-if="form.avatar" class="inline-list">
        <img :src="form.avatar" alt="avatar" class="avatar" />
        <span class="muted">头像预览</span>
      </div>
      <button class="cta" :disabled="loading" @click="onSubmit">
        {{ loading ? '注册中...' : '注册并登录' }}
      </button>
      <div v-if="errorMessage" class="muted">{{ errorMessage }}</div>
    </div>
  </div>
</template>
