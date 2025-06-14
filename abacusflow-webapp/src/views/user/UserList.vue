<template>
  <div>
    <a-button type="primary" @click="showCreateModal" style="margin-bottom: 16px">
      新增用户
    </a-button>
    <a-table :columns="columns" :data-source="data" :loading="isPending" :row-key="'id'">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'createdAt'">
          <span>
            {{ $formatDate(record.createdAt) }}
          </span>
        </template>
        <template v-else-if="column.key === 'action'">
          <span>
            <a @click="handleEdit(record)">修改</a>
            <a-divider type="vertical" />
            <a @click="handleDelete(record)">删除</a>
          </span>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { UserApi } from '@/core/openapi'
import { useQuery } from '@tanstack/vue-query'
import { inject } from 'vue'

const emit = defineEmits(['edit', 'delete', 'create'])

const userApi = inject('userApi') as UserApi

const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '姓名',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const { isPending, data } = useQuery({
  queryKey: ['users'],
  queryFn: () => userApi.listUsers(),
})

const handleEdit = (record: any) => {
  emit('edit', record)
}

const handleDelete = (record: any) => {
  emit('delete', record)
}

const showCreateModal = () => {
  emit('create')
}
</script> 