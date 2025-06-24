<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>供应商管理</h1>
        <a-button type="primary" @click="handleAddSupplier" style="margin-bottom: 16px">
          新增供应商
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="供应商名">
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
        <a-table
          :columns="columns"
          :data-source="filteredData"
          :loading="isPending"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'locked'">
              <a-switch v-model:checked="record.locked" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditSupplier(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm title="确定删除该供应商？" @confirm="handleDeleteSupplier(record.id)">
                  <a-button type="link" shape="circle">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增供应商" :open="showAdd" :closable="false" @close="showAdd = false">
      <SupplierAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改供应商" :open="showEdit" :closable="false" @close="showEdit = false">
      <SupplierEditView
        v-if="showEdit && editingSupplier"
        v-model:visible="showEdit"
        :supplierId="editingSupplier.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import {computed, inject, ref} from "vue";
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import type {BasicSupplier, PartnerApi, Supplier} from "@/core/openapi";
import SupplierAddView from "./SupplierAddView.vue";
import SupplierEditView from "./SupplierEditView.vue";
import type {StrictTableColumnsType} from "@/core/antdv/antdev-table";
import {message} from "ant-design-vue";

const partnerApi = inject("partnerApi") as PartnerApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingSupplier = ref<Supplier | null>(null);
// 搜索表单
const searchForm = ref({
  name: "",
  categoryId: undefined
});

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: "",
    categoryId: undefined
  };
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

const handleAddSupplier = () => (showAdd.value = true);
const handleEditSupplier = (supplier: Supplier) => {
  editingSupplier.value = supplier;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["suppliers"],
  queryFn: () => partnerApi.listSuppliers()
});

const filteredData = computed(() => {
  const name = searchForm.value.name.trim().toLowerCase();
  if (!name) return data.value;

  return data.value?.filter((supplier) => {
    return supplier.name.toLowerCase().includes(name);
  });
});

const { mutate: deleteSupplier } = useMutation({
  mutationFn: (id: number) => partnerApi.deleteSupplier({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["suppliers"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteSupplier(id: number) {
  deleteSupplier(id);
}

const columns: StrictTableColumnsType<BasicSupplier> = [
  { title: "供应商名", dataIndex: "name", key: "name" },
  { title: "联系人", dataIndex: "contactPerson", key: "contactPerson" },
  { title: "联系电话", dataIndex: "phone", key: "phone" },
  { title: "联系地址", dataIndex: "address", key: "address" },
  { title: "操作", key: "action" }
];
</script>
