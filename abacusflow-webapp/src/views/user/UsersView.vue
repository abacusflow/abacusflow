<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>用户管理</h1>
        <a-button type="primary" @click="handleAddUser" style="margin-bottom: 16px">
          新增用户
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="姓名">
            <a-input v-model:value="searchForm.keyword" placeholder="请输入姓名" allow-clear />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">搜索</a-button>
              <a-button @click="resetSearch">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <a-card :bordered="false">
        <a-table :columns="columns" :data-source="filteredData" :loading="isPending" row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'locked'">
              <a-switch v-model:checked="record.locked" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button
                  type="link"
                  shape="circle"
                  :disabled="record.name === 'admin'"
                  @click="handleEditUser(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定删除该用户？"
                  @confirm="handleDeleteUser(record.id)"
                  :disabled="record.name === 'admin'"
                >
                  <a-button type="link" shape="circle" :disabled="record.name === 'admin'"
                    >删除</a-button
                  >
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增用户" :open="showAdd" :closable="false" @close="showAdd = false">
      <UserAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改用户" :open="showEdit" :closable="false" @close="showEdit = false">
      <UserEditView
        v-if="showEdit && editingUser"
        v-model:visible="showEdit"
        :userId="editingUser.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { computed, inject, ref } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { BasicUser, User, UserApi } from "@/core/openapi";
import UserAddView from "./UserAddView.vue";
import UserEditView from "./UserEditView.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";

const userApi = inject("userApi") as UserApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingUser = ref<User | null>(null);
// 搜索表单
const searchForm = ref({
  keyword: "",
  categoryId: undefined
});

// 搜索
const handleSearch = () => {
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: "",
    categoryId: undefined
  };
  refetch();
};

const handleAddUser = () => (showAdd.value = true);
const handleEditUser = (user: User) => {
  editingUser.value = user;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["users"],
  queryFn: () => userApi.listUsers()
});

const filteredData = computed(() => {
  const keyword = searchForm.value.keyword.trim().toLowerCase();
  if (!keyword) return data.value;

  return data.value?.filter((user) => {
    return user.name.toLowerCase().includes(keyword) || user.nick.toLowerCase().includes(keyword);
  });
});

const { mutate: deleteUser } = useMutation({
  mutationFn: (id: number) => userApi.deleteUser({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["users"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteUser(id: number) {
  deleteUser(id);
}

const columns: StrictTableColumnsType<BasicUser> = [
  { title: "用户名", dataIndex: "name", key: "name" },
  { title: "姓名", dataIndex: "nick", key: "nick" },
  { title: "启用状态", dataIndex: "enabled", key: "enabled" },
  { title: "锁定状态", dataIndex: "locked", key: "locked" },
  { title: "操作", key: "action" }
];
</script>
