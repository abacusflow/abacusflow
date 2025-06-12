<template>
  <!-- <a-table :columns="columns" :data-source="data">
    <template #headerCell="{ column }">
      <template v-if="column.key === 'name'">
        <span>
          <smile-outlined />
          Name
        </span>
      </template>
    </template>

    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'name'">
        <a>
          {{ record.name }}
        </a>
      </template>
      <template v-else-if="column.key === 'action'">
        <span>
          <a>Invite 一 {{ record.name }}</a>
          <a-divider type="vertical" />
          <a>Delete</a>
          <a-divider type="vertical" />
          <a class="ant-dropdown-link">
            More actions
            <down-outlined />
          </a>
        </span>
      </template>
    </template>
  </a-table> -->
  <span v-if="isPending">Loading...</span>
  <span v-else-if="isError">Error: {{ error.message }}</span>
  <!-- We can assume by this point that `isSuccess === true` -->
  <ul v-else>
    <li v-for="user in data" :key="user.id">{{ user.username }}</li>
  </ul>
</template>
<script lang="ts" setup>
import { UserApi } from '@/core/openapi'
import { useQuery } from '@tanstack/vue-query'
import { inject } from 'vue'

const userApi = inject('userApi') as UserApi

const { isPending, isError, data, error } = useQuery({
  queryKey: ['users'],
  queryFn: () => userApi.listUsers(),
})
</script>
