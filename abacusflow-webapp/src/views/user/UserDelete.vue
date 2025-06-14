<template>
  <a-modal
    title="删除用户"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
  >
    <p>确定要删除用户 "{{ userData?.username }}" 吗？</p>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { UserApi } from '@/core/openapi'
import { inject } from 'vue'

const props = defineProps<{
  visible: boolean
  userData?: any
}>()

const emit = defineEmits(['update:visible', 'success'])

const userApi = inject('userApi') as UserApi
const loading = ref(false)

const handleOk = async () => {
  try {
    loading.value = true
    await userApi.deleteUser(props.userData.id)
    emit('success')
    handleCancel()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('update:visible', false)
}
</script> 