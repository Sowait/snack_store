<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AdminLayout from './layouts/AdminLayout.vue'
import FrontLayout from './layouts/FrontLayout.vue'

const route = useRoute()
const isAdmin = computed(() => route.path.startsWith('/admin'))
</script>

<template>
  <component :is="isAdmin ? AdminLayout : FrontLayout" />
</template>
