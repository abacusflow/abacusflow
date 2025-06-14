<template>
  <div class="warehouse-create">
    <div class="header">
      <h1>新增仓库</h1>
    </div>

    <a-card>
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="仓库名称" name="name">
          <a-input v-model:value="form.name" placeholder="请输入仓库名称" />
        </a-form-item>
        <a-form-item label="位置" name="location">
          <a-input v-model:value="form.location" placeholder="请输入位置" />
        </a-form-item>
        <a-form-item label="容量" name="capacity">
          <a-input-number
            v-model:value="form.capacity"
            :min="0"
            style="width: 100%"
            placeholder="请输入容量"
          />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 6 }">
          <a-space>
            <a-button type="primary" @click="handleSubmit">保存</a-button>
            <a-button @click="handleCancel">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import type { FormInstance } from 'ant-design-vue'
import { WarehouseApi } from '@/core/openapi/apis'
import type { CreateWarehouseInput } from '@/core/openapi/models'

const router = useRouter()
const warehouseApi = new WarehouseApi()

const formRef = ref<FormInstance>()
const form = ref<CreateWarehouseInput>({
  name: '',
  location: null,
  capacity: null
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入仓库名称' }],
  location: [{ required: true, message: '请输入位置' }],
  capacity: [{ required: true, message: '请输入容量' }]
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    await warehouseApi.addWarehouse({ createWarehouseInput: form.value })
    message.success('新增成功')
    router.push('/warehouse')
  } catch (error) {
    message.error('新增失败')
  }
}

// 取消
const handleCancel = () => {
  router.push('/warehouse')
}
</script>

<style scoped>
.warehouse-create {
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}
</style> 