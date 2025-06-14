<template>
  <a-modal v-model:open="open">
    <div class="product-create">
      <div class="header">
        <h1>新增产品</h1>
      </div>

      <a-card>
        <a-form
          ref="formRef"
          :model="form"
          :rules="rules"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 16 }"
        >
          <a-form-item label="产品名称" name="name">
            <a-input v-model:value="form.name" placeholder="请输入产品名称" />
          </a-form-item>
          <a-form-item label="分类" name="categoryId">
            <a-select v-model:value="form.categoryId" placeholder="请选择分类">
              <a-select-option
                v-for="category in categories"
                :key="category.id"
                :value="category.id"
              >
                {{ category.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="单位" name="unit">
            <a-input v-model:value="form.unit" placeholder="请输入单位" />
          </a-form-item>
          <a-form-item label="价格" name="price">
            <a-input-number
              v-model:value="form.price"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="描述" name="description">
            <a-textarea v-model:value="form.description" :rows="3" placeholder="请输入描述" />
          </a-form-item>
          <a-form-item :wrapper-col="{ offset: 6 }">
            <a-space>
              <a-button type="primary" :loading="isPending" @click="handleSubmit">保存</a-button>
              <a-button @click="handleCancel">取消</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import type { ProductApi } from '@/core/openapi/apis'
import type { CreateProductInput } from '@/core/openapi/models'

const router = useRouter()
const productApi = inject('productApi') as ProductApi
const queryClient = useQueryClient()
const formRef = ref<FormInstance>()

const open = ref<boolean>(true)

// 表单数据
const form = ref<CreateProductInput>({
  name: '',
  categoryId: undefined,
  unit: '',
  price: 0,
  description: '',
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入产品名称' }],
  categoryId: [{ required: true, message: '请选择分类' }],
  unit: [{ required: true, message: '请输入单位' }],
  price: [{ required: true, message: '请输入价格' }],
}

// 使用 Vue Query 获取分类列表
const { data: categories } = useQuery({
  queryKey: ['categories'],
  queryFn: () => productApi.listProductCategories(),
  onError: () => {
    message.error('获取分类列表失败')
  },
})

// 使用 Vue Query 添加产品
const { isPending, mutate: addProduct } = useMutation({
  mutationFn: (input: CreateProductInput) => productApi.addProduct({ createProductInput: input }),
  onSuccess: () => {
    message.success('新增成功')
    queryClient.invalidateQueries({ queryKey: ['products'] })
    router.push('/product')
  },
  onError: () => {
    message.error('新增失败')
  },
})

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    addProduct(form.value)
  } catch (error) {
    // 表单验证失败
  }
}

// 取消
const handleCancel = () => {
  router.push('/product')
}
</script>

<style scoped>
.product-create {
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}
</style>
