<template>
  <a-modal
    :title="'修改用户'"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
  >
    <a-form :model="formState" :rules="rules" ref="formRef">
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
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { UserApi } from '@/core/openapi'
import { inject } from 'vue'
import type { FormInstance } from 'ant-design-vue'

const props = defineProps<{ visible: boolean }>()
const route = useRoute()
const userApi = inject('userApi') as UserApi
const emit = defineEmits(['update:visible', 'success'])

const formRef = ref<FormInstance>()
const loading = ref(false)
const userId = route.params.id as string

const formState = reactive({
  username: '',
  nickname: '',
  password: '',
})

const rules = {
  nickname: [{ required: true, message: '请输入姓名' }],
  password: [{ required: true, message: '请输入密码' }],
}

const fetchUserDetail = async () => {
  try {
    const user = await userApi.getUser(userId)
    formState.username = user.username
    formState.nickname = user.nickname
    // 如果需要，也可以填充 password 或其它字段
  } catch (e) {
    console.error('用户信息获取失败', e)
  }
}

onMounted(() => {
  fetchUserDetail()
})

const handleOk = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    await userApi.updateUser(userId, {
      nickname: formState.nickname,
      password: formState.password,
    })
    emit('success')
    handleCancel()

    // success feedback，比如返回用户列表
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
