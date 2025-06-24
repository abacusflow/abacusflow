<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>储存点管理</h1>
        <a-button type="primary" @click="handleAddDepot" style="margin-bottom: 16px">
          新增储存点
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="关键字名称">
            <a-input v-model:value="searchForm.name" placeholder="请输入关键字" allow-clear />
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
        <a-table
          :columns="columns"
          :data-source="data"
          :loading="isPending"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditDepot(record)"
                  >编辑
                </a-button>

                <a-divider type="vertical" />

                <a-popconfirm title="确定删除该储存点？" @confirm="handleDeleteDepot(record.id)">
                  <a-button type="link" shape="circle">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增储存点" :open="showAdd" :closable="false" @close="showAdd = false">
      <DepotAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改储存点" :open="showEdit" :closable="false" @close="showEdit = false">
      <DepotEditView
        v-if="showEdit && editingDepot"
        v-model:visible="showEdit"
        :depotId="editingDepot.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import {inject, ref} from "vue";
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import type {BasicDepot, Depot, DepotApi} from "@/core/openapi";
import DepotAddView from "./DepotAddView.vue";
import DepotEditView from "./DepotEditView.vue";
import type {StrictTableColumnsType} from "@/core/antdv/antdev-table";
import {message} from "ant-design-vue";

const depotApi = inject("depotApi") as DepotApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingDepot = ref<Depot | null>(null);
// 搜索表单
const searchForm = ref({
  name: ""
});

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["depots"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: ""
  };
  queryClient.invalidateQueries({ queryKey: ["depots"] });
  refetch();
};

const handleAddDepot = () => (showAdd.value = true);
const handleEditDepot = (depot: Depot) => {
  editingDepot.value = depot;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["depots"],
  queryFn: () => depotApi.listDepots()
});

const { mutate: deleteDepot } = useMutation({
  mutationFn: (id: number) => depotApi.deleteDepot({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["depots"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteDepot(id: number) {
  deleteDepot(id);
}

const columns: StrictTableColumnsType<BasicDepot> = [
  { title: "储存点名称", dataIndex: "name", key: "name" },
  { title: "储存点地址", dataIndex: "location", key: "location" },
  { title: "储存点容量", dataIndex: "capacity", key: "capacity" },
  { title: "启用状态", dataIndex: "enabled", key: "enabled" },
  { title: "操作", key: "action" }
];
</script>
