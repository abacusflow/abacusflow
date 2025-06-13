<template>
  <div class="warehouse">
    <div class="header">
      <h1>仓库管理</h1>
      <a-button type="primary" @click="handleAdd">新增仓库</a-button>
    </div>

    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="仓库名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入仓库名称"
            allow-clear
          />
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
        :loading="loading"
        :dataSource="warehouses"
        :columns="columns"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除这个仓库吗？"
                @confirm="handleDelete(record)"
              >
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="modalType === 'add' ? '新增仓库' : '编辑仓库'"
      @ok="handleSubmit"
      @cancel="modalVisible = false"
    >
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
        <a-form-item label="地址" name="address">
          <a-input v-model:value="form.address" placeholder="请输入地址" />
        </a-form-item>
        <a-form-item label="联系人" name="contact">
          <a-input v-model:value="form.contact" placeholder="请输入联系人" />
        </a-form-item>
        <a-form-item label="联系电话" name="phone">
          <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="form.description"
            :rows="3"
            placeholder="请输入描述"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { WarehouseApi } from '@/core/openapi/apis'
import type { BasicWarehouse, Warehouse, CreateWarehouseInput, UpdateWarehouseInput } from '@/core/openapi/models'

const warehouseApi = new WarehouseApi()

// 数据列表
const warehouses = ref<BasicWarehouse[]>([])
const loading = ref(false)

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '仓库名称',
    dataIndex: 'name',
  },
  {
    title: '地址',
    dataIndex: 'address',
    ellipsis: true,
  },
  {
    title: '联系人',
    dataIndex: 'contact',
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
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
  name: ''
})

// 对话框相关
const modalVisible = ref(false)
const modalType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = ref<CreateWarehouseInput | UpdateWarehouseInput>({
  name: '',
  address: '',
  contact: '',
  phone: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入仓库名称' }],
  address: [{ required: true, message: '请输入地址' }],
  contact: [{ required: true, message: '请输入联系人' }],
  phone: [{ required: true, message: '请输入联系电话' }]
}

// 获取仓库列表
const getWarehouses = async () => {
  loading.value = true
  try {
    const response = await warehouseApi.listWarehouses()
    warehouses.value = response
  } catch (error) {
    message.error('获取仓库列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getWarehouses()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: ''
  }
  getWarehouses()
}

// 新增仓库
const handleAdd = () => {
  modalType.value = 'add'
  form.value = {
    name: '',
    address: '',
    contact: '',
    phone: '',
    description: ''
  }
  modalVisible.value = true
}

// 编辑仓库
const handleEdit = (record: BasicWarehouse) => {
  modalType.value = 'edit'
  form.value = {
    name: record.name,
    address: record.address,
    contact: record.contact,
    phone: record.phone,
    description: record.description
  }
  modalVisible.value = true
}

// 删除仓库
const handleDelete = async (record: BasicWarehouse) => {
  try {
    await warehouseApi.deleteWarehouse({ id: record.id })
    message.success('删除成功')
    getWarehouses()
  } catch (error) {
    message.error('删除失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (modalType.value === 'add') {
      await warehouseApi.addWarehouse({ createWarehouseInput: form.value as CreateWarehouseInput })
      message.success('新增成功')
    } else {
      const warehouse = warehouses.value.find(w => w.name === form.value.name)
      if (warehouse) {
        await warehouseApi.updateWarehouse({
          id: warehouse.id,
          updateWarehouseInput: form.value as UpdateWarehouseInput
        })
        message.success('编辑成功')
      }
    }
    modalVisible.value = false
    getWarehouses()
  } catch (error) {
    message.error(modalType.value === 'add' ? '新增失败' : '编辑失败')
  }
}

onMounted(() => {
  getWarehouses()
})
</script>

<style scoped>
.warehouse {
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