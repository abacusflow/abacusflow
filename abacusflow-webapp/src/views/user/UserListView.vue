<template>
  <div>
    <a-button type="primary" @click="handleAdd()" style="margin-bottom: 16px"> 新增用户 </a-button>
    <a-table :columns="columns" :data-source="data" :loading="isPending" :row-key="'id'">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'createdAt'">
          <span>
            {{ $formatDate(record.createdAt) }}
          </span>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定要删除这个记录吗？" @confirm="handleDelete(record)">
              <a class="danger-link">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { UserApi, type User } from '@/core/openapi'
import { useQuery } from '@tanstack/vue-query'
import { inject } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
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

// 新增产品
const handleAdd = () => {
  router.push('/user/add')
}
const handleEdit = (record: User) => {
  router.push(`/user/edit/${record.id}`)
}

const handleDelete = (record: User) => {
  emit('delete', record)
}
</script>
