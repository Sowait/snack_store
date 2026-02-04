<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useShopStore } from '../../store/shopStore'

const store = useShopStore()
const currentAdmin = store.currentAdmin

const pageSize = ref(10)
const page = ref(1)
const total = computed(() => store.state.users.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const pagedUsers = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return store.state.users.slice(start, start + pageSize.value)
})

watch(pageSize, () => {
  page.value = 1
})

watch([total, totalPages], () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

const modalOpen = ref(false)
const loading = ref(false)
const errorMessage = ref('')

const userForm = reactive({
  id: '',
  username: '',
  phone: '',
  email: '',
  status: '启用'
})

const openEdit = (user) => {
  errorMessage.value = ''
  userForm.id = user.id
  userForm.username = user.username || ''
  userForm.phone = user.phone || ''
  userForm.email = user.email || ''
  userForm.status = user.status || '启用'
  modalOpen.value = true
}

const closeModal = () => {
  modalOpen.value = false
}

const onSubmit = async () => {
  if (loading.value) return
  if (!userForm.id) return
  if (!userForm.username.trim() || !userForm.phone.trim()) return
  loading.value = true
  errorMessage.value = ''
  const ok = await store
    .updateUser(userForm.id, {
      username: userForm.username.trim(),
      phone: userForm.phone.trim(),
      email: userForm.email.trim(),
      status: userForm.status
    })
    .then(() => true)
    .catch((e) => {
      errorMessage.value = e?.message || '保存失败'
      return false
    })
    .finally(() => {
      loading.value = false
    })
  if (ok) closeModal()
}
</script>

<template>
  <div>
    <div class="page-title">用户管理</div>
    <div v-if="!currentAdmin" class="muted">请先在后台登录</div>
    <div v-else>
      <table class="table">
        <thead>
          <tr>
            <th>用户</th>
            <th>手机号</th>
            <th>邮箱</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in pagedUsers" :key="user.id">
            <td>
              <div style="font-weight: 700">{{ user.username }}</div>
              <div class="muted">{{ user.id }}</div>
            </td>
            <td>{{ user.phone }}</td>
            <td>{{ user.email }}</td>
            <td><span class="status">{{ user.status }}</span></td>
            <td class="inline-list">
              <button class="ghost" type="button" @click="openEdit(user)">编辑</button>
              <button
                v-if="user.status === '启用'"
                class="ghost"
                @click="store.updateUserStatus(user.id, '禁用')"
              >
                禁用
              </button>
              <button
                v-else
                class="ghost"
                @click="store.updateUserStatus(user.id, '启用')"
              >
                启用
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="toolbar" style="margin-top: 12px">
        <div class="inline-list" style="margin-left: auto">
          <div class="muted">共 {{ total }} 条</div>
          <select v-model.number="pageSize">
            <option :value="5">5 / 页</option>
            <option :value="10">10 / 页</option>
            <option :value="20">20 / 页</option>
          </select>
          <button class="ghost" type="button" :disabled="page <= 1" @click="page--">上一页</button>
          <div class="muted">{{ page }} / {{ totalPages }}</div>
          <button class="ghost" type="button" :disabled="page >= totalPages" @click="page++">下一页</button>
        </div>
      </div>

      <div v-if="modalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal">
          <div class="modal-head">
            <div style="font-weight: 900">编辑用户</div>
            <button class="ghost" type="button" @click="closeModal">关闭</button>
          </div>
          <div class="modal-body">
            <div class="form">
              <div class="grid-2">
                <input v-model.trim="userForm.username" placeholder="用户名" />
                <select v-model="userForm.status">
                  <option value="启用">启用</option>
                  <option value="禁用">禁用</option>
                </select>
              </div>
              <div class="grid-2">
                <input v-model.trim="userForm.phone" placeholder="手机号" />
                <input v-model.trim="userForm.email" placeholder="邮箱" />
              </div>
              <div v-if="errorMessage" class="form-error">{{ errorMessage }}</div>
              <div class="toolbar">
                <button class="ghost" type="button" @click="closeModal">取消</button>
                <button class="cta" type="button" :disabled="loading" @click="onSubmit">
                  {{ loading ? '保存中...' : '保存' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
