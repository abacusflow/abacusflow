<template>
  <div class="product">
    <div class="header">
      <h1>产品管理</h1>
      <a-button type="primary" @click="handleAdd">新增产品</a-button>
    </div>

    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="产品名称">
          <a-input v-model:value="searchForm.name" placeholder="请输入产品名称" allow-clear />
        </a-form-item>
        <a-form-item label="分类">
          <a-select
            v-model:value="searchForm.categoryId"
            placeholder="请选择分类"
            allow-clear
            style="width: 200px"
          >
            <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">搜索</a-button>
            <a-button @click="resetSearch">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="table-card">
      <a-table
        :loading="isPending"
        :dataSource="productsData"
        :columns="columns"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除这个产品吗？" @confirm="handleDelete(record)">
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import type { ProductApi } from '@/core/openapi/apis'
import type { BasicProduct } from '@/core/openapi/models'

const router = useRouter()
const productApi = inject('productApi') as ProductApi
const queryClient = useQueryClient()

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '产品名称',
    dataIndex: 'name',
  },
  {
    title: '分类',
    dataIndex: ['category', 'name'],
  },
  {
    title: '单位',
    dataIndex: 'unit',
  },
  {
    title: '价格',
    dataIndex: 'price',
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 搜索表单
const searchForm = ref({
  name: '',
  categoryId: undefined,
})

// 使用 Vue Query 获取产品列表
const { isPending, data: productsData } = useQuery({
  queryKey: ['products'],
  queryFn: () => productApi.listProducts(),
})

// 使用 Vue Query 获取分类列表
const { data: categories } = useQuery({
  queryKey: ['categories'],
  queryFn: () => productApi.listCategories(),
  onError: () => {
    message.error('获取分类列表失败')
  },
})

// 使用 Vue Query 删除产品
const deleteProductMutation = useMutation({
  mutationFn: (id: number) => productApi.deleteProduct({ id }),
  onSuccess: () => {
    message.success('删除成功')
    queryClient.invalidateQueries({ queryKey: ['products'] })
  },
  onError: () => {
    message.error('删除失败')
  },
})

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ['products'] })
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: '',
    categoryId: undefined,
  }
  queryClient.invalidateQueries({ queryKey: ['products'] })
}

// 新增产品
const handleAdd = () => {
  router.push('/product/create')
}

// 编辑产品
const handleEdit = (record: BasicProduct) => {
  router.push(`/product/edit/${record.id}`)
}

// 删除产品
const handleDelete = (record: BasicProduct) => {
  deleteProductMutation.mutate(record.id)
}
</script>

<style scoped>
.product {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-card {
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.danger-link {
  color: #ff4d4f;
}

.danger-link:hover {
  color: #ff7875;
}
</style>
