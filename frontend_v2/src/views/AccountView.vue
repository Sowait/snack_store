<script setup>
import { reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '../store/shopStore'

const store = useShopStore()
const router = useRouter()
const currentUser = store.currentUser

const profileForm = reactive({
  username: '',
  phone: '',
  email: '',
  avatar: ''
})

const addressForm = reactive({
  name: '',
  phone: '',
  region: '',
  detail: '',
  isDefault: false
})

const addresses = store.currentUserAddresses

watch(
  () => store.currentUser.value,
  (user) => {
    profileForm.username = user?.username || ''
    profileForm.phone = user?.phone || ''
    profileForm.email = user?.email || ''
    profileForm.avatar = user?.avatar || ''
  },
  { immediate: true }
)

const onProfileUpload = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    profileForm.avatar = URL.createObjectURL(file)
  }
}

const onSaveProfile = async () => {
  await store.updateUserProfile({ ...profileForm })
}

const onAddAddress = async () => {
  if (!addressForm.name || !addressForm.phone || !addressForm.region || !addressForm.detail) return
  await store.addAddress({ ...addressForm })
  addressForm.name = ''
  addressForm.phone = ''
  addressForm.region = ''
  addressForm.detail = ''
  addressForm.isDefault = false
}

const onLogout = async () => {
  await store.logoutUser()
  router.push('/')
}
</script>

<template>
  <div>
    <div class="page-title">个人中心</div>
    <div v-if="!currentUser" class="muted">请先登录</div>
    <div v-else class="split">
      <div class="card">
        <div class="muted">个人信息</div>
        <div class="form">
          <div class="inline-list">
            <img v-if="profileForm.avatar" :src="profileForm.avatar" alt="avatar" class="avatar" />
            <input type="file" accept="image/*" @change="onProfileUpload" />
          </div>
          <input v-model="profileForm.username" placeholder="用户名" />
          <input v-model="profileForm.phone" placeholder="手机号" disabled />
          <input v-model="profileForm.email" placeholder="邮箱" />
          <div class="inline-list">
            <button class="cta" @click="onSaveProfile">保存资料</button>
            <button class="ghost" type="button" @click="onLogout">退出登录</button>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="muted">收货地址</div>
        <div class="form">
          <input v-model="addressForm.name" placeholder="收货人" />
          <input v-model="addressForm.phone" placeholder="联系电话" />
          <input v-model="addressForm.region" placeholder="省市区" />
          <input v-model="addressForm.detail" placeholder="详细地址" />
          <label class="pill">
            <input type="checkbox" v-model="addressForm.isDefault" />
            设为默认地址
          </label>
          <button class="ghost" @click="onAddAddress">新增地址</button>
        </div>
        <div class="section" v-if="addresses.length">
          <div v-for="addr in addresses" :key="addr.id" class="card">
            <div class="inline-list">
              <span style="font-weight: 700">{{ addr.name }} {{ addr.phone }}</span>
              <span v-if="addr.isDefault" class="pill">默认</span>
            </div>
            <div class="muted">{{ addr.region }} {{ addr.detail }}</div>
            <div class="inline-list">
              <button class="ghost" @click="store.setDefaultAddress(addr.id)">设为默认</button>
              <button class="ghost" @click="store.removeAddress(addr.id)">删除</button>
            </div>
          </div>
        </div>
        <div v-else class="muted">暂无地址</div>
      </div>
    </div>
  </div>
</template>
