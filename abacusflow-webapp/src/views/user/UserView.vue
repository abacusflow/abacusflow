<template>
  <a-table :columns="columns" :data-source="data" :loading="isPending" :row-key="'id'">
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'createdAt'">
        <span>
          {{ $formatDate(record.createdAt) }}
        </span>
      </template>
      <template v-else-if="column.key === 'action'">
        <span>
          <a>修改</a>
          <a-divider type="vertical" />
          <a>删除</a>
        </span>
      </template>
    </template>
  </a-table>
</template>
<script lang="ts" setup>
import {UserApi} from '@/core/openapi'
import {useQuery} from '@tanstack/vue-query'
import {inject} from 'vue'

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

const { isPending, isError, data, error } = useQuery({
  queryKey: ['users'],
  queryFn: () => userApi.listUsers(),
})
</script>
