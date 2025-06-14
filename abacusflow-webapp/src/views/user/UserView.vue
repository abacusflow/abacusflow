<template>
  <div>
    <UserList @edit="handleEdit" @delete="handleDelete" @create="handleCreate" />

    <UserForm
      v-model:visible="formVisible"
      :is-edit="isEdit"
      :user-data="selectedUser"
      @success="handleSuccess"
    />

    <UserDelete
      v-model:visible="deleteVisible"
      :user-data="selectedUser"
      @success="handleSuccess"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import UserList from './UserList.vue'
import UserForm from './UserForm.vue'
import UserDelete from './UserDelete.vue'

const formVisible = ref(false)
const deleteVisible = ref(false)
const isEdit = ref(false)
const selectedUser = ref<any>(null)

const handleEdit = (user: any) => {
  selectedUser.value = user
  isEdit.value = true
  formVisible.value = true
}

const handleDelete = (user: any) => {
  selectedUser.value = user
  deleteVisible.value = true
}

const handleCreate = () => {
  selectedUser.value = null
  isEdit.value = false
  formVisible.value = true
}

const handleSuccess = () => {
  // Refresh the list
  // The list will automatically refresh due to the query invalidation
}
</script>
