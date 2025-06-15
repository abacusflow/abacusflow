<template>
  <div>
    <a-button type="primary" @click="openCreateModal" style="margin-bottom: 16px">
      新增用户
    </a-button>
    <a-table :columns="columns" :data-source="data?.items" :loading="isPending" :row-key="'id'">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'createdAt'">
          {{ $formatDate(record.createdAt) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="openEditModal(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该用户？" @confirm="handleDelete(record.id)">
              <a class="danger-link">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <UserAddView v-model:visible="showCreate" @success="refetch" />
    <UserEditView v-model:visible="showEdit" @success="refetch" />
  </div>
</template>

<script lang="ts" setup>
import { ref, inject } from 'vue'
import { useQuery, useQueryClient } from '@tanstack/vue-query'
import type { UserApi, User } from '@/core/openapi'
import UserAddView from './UserAddView.vue'
import UserEditView from './UserEditView.vue'

const userApi = inject('userApi') as UserApi
const queryClient = useQueryClient()

const showCreate = ref(false)
const showEdit = ref(false)
const editingUser = ref<User | null>(null)

const openCreateModal = () => (showCreate.value = true)
const openEditModal = (user: User) => {
  editingUser.value = user
  showEdit.value = true
}

const { data, isPending, refetch } = useQuery({
  queryKey: ['users'],
  queryFn: () => userApi.listUsers(),
})

const handleDelete = async (id: number) => {
  await userApi.deleteUser({ id })
  queryClient.invalidateQueries({ queryKey: ['users'] })
}

const columns = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'nickname', key: 'nickname' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action' },
]
</script>
