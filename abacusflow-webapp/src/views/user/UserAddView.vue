<template>
  <a-modal
    :title="'新增用户'"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
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
import { ref, reactive, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import { UserApi } from '@/core/openapi'
import { inject } from 'vue'

const props = defineProps<{
  visible: boolean
  userData?: any
}>()

const emit = defineEmits(['update:visible', 'success'])

const userApi = inject('userApi') as UserApi
const formRef = ref<FormInstance>()
const loading = ref(false)

const formState = reactive({
  username: '',
  nickname: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  nickname: [{ required: true, message: '请输入姓名' }],
  password: [{ required: true, message: '请输入密码' }],
}

watch(
  () => props.userData,
  (newVal) => {
    if (newVal) {
      formState.username = newVal.username
      formState.nickname = newVal.nickname
    }
  },
)

const handleOk = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    await userApi.createUser({
      username: formState.username,
      nickname: formState.nickname,
      password: formState.password,
    })

    emit('success')
    handleCancel()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  formRef.value?.resetFields()
  emit('update:visible', false)
}
</script>
