<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>仓库管理</h1>
        <a-button type="primary" @click="handleAddWarehouse" style="margin-bottom: 16px">
          新增仓库
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="仓库名称">
            <a-input v-model:value="searchForm.name" placeholder="请输入姓名" allow-clear />
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
        <a-table :columns="columns" :data-source="data" :loading="isPending" :row-key="'id'">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" @click="handleEditWarehouse(record)">编辑 </a-button>

                <a-divider type="vertical" />

                <a-popconfirm title="确定删除该仓库？" @confirm="handleDeleteWarehouse(record.id)">
                  <a-button type="link">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增仓库" :open="showAdd" :closable="false" @close="showAdd = false">
      <WarehouseAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改仓库" :open="showEdit" :closable="false" @close="showEdit = false">
      <WarehouseEditView
        v-if="showEdit && editingWarehouse"
        v-model:visible="showEdit"
        :warehouseId="editingWarehouse.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { ref, inject } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { WarehouseApi, Warehouse, BasicWarehouse } from "@/core/openapi";
import WarehouseAddView from "./WarehouseAddView.vue";
import WarehouseEditView from "./WarehouseEditView.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";

const warehouseApi = inject("warehouseApi") as WarehouseApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingWarehouse = ref<Warehouse | null>(null);
// 搜索表单
const searchForm = ref({
  name: ""
});

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["warehouses"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: ""
  };
  queryClient.invalidateQueries({ queryKey: ["warehouses"] });
  refetch();
};

const handleAddWarehouse = () => (showAdd.value = true);
const handleEditWarehouse = (warehouse: Warehouse) => {
  editingWarehouse.value = warehouse;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["warehouses"],
  queryFn: () => warehouseApi.listWarehouses()
});

const { mutate: deleteWarehouse } = useMutation({
  mutationFn: (id: number) => warehouseApi.deleteWarehouse({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["warehouses"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteWarehouse(id: number) {
  deleteWarehouse(id);
}

const columns: StrictTableColumnsType<BasicWarehouse> = [
  { title: "仓库名称", dataIndex: "name", key: "name" },
  { title: "仓库地址", dataIndex: "location", key: "location" },
  { title: "仓库容量", dataIndex: "capacity", key: "capacity" },
  { title: "启用状态", dataIndex: "enabled", key: "enabled" },
  { title: "操作", key: "action" }
];
</script>
