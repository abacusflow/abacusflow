<template>
  <a-modal
    title="新增用户"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirm-loading="loading"
  >
    <a-form :model="formState" :rules="rules" ref="formRef">
      <a-form-item label="用户名" name="username">
        <a-input v-model:value="formState.username" />
      </a-form-item>
      <a-form-item label="姓名" name="nickname">
        <a-input v-model:value="formState.nickname" />
      </a-form-item>
      <a-form-item label="密码" name="password">
        <a-input-password v-model:value="formState.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import { inject } from 'vue'
import type { UserApi } from '@/core/openapi'

const props = defineProps<{ visible: boolean }>()
const emit = defineEmits(['update:visible', 'success'])

const userApi = inject('userApi') as UserApi
const formRef = ref<FormInstance>()
const loading = ref(false)

const formState = reactive({ username: '', nickname: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  nickname: [{ required: true, message: '请输入姓名' }],
  password: [{ required: true, message: '请输入密码' }],
}

const handleOk = async () => {
  await formRef.value?.validate()
  loading.value = true
  await userApi.createUser({ ...formState })
  emit('success')
  handleCancel()
  loading.value = false
}

const handleCancel = () => {
  formRef.value?.resetFields()
  emit('update:visible', false)
}
</script>
