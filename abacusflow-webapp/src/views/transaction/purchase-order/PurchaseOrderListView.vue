<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>采购单管理</h1>
        <a-button type="primary" @click="handleAddPurchaseOrder" style="margin-bottom: 16px">
          新增采购单
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="采购单号">
            <a-input v-model:value="searchForm.no" placeholder="请输入采购单号" allow-clear />
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
          :row-key="'id'"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'orderDate'">
              {{ $dateToFormattedString(record.orderDate, "YYYY-MM-DD") }}
            </template>

            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditPurchaseOrder(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定删除该采购单？"
                  @confirm="handleDeletePurchaseOrder(record.id)"
                >
                  <a-button type="link" shape="circle">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer
      title="新增采购单"
      width="500"
      :open="showAdd"
      :closable="false"
      @close="showAdd = false"
    >
      <PurchaseOrderAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer
      title="修改采购单"
      width="500"
      :open="showEdit"
      :closable="false"
      @close="showEdit = false"
    >
      <PurchaseOrderEditView
        v-if="showEdit && editingPurchaseOrder"
        v-model:visible="showEdit"
        :purchaseOrderId="editingPurchaseOrder.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import {computed, inject, ref} from "vue";
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import type {BasicPurchaseOrder, TransactionApi} from "@/core/openapi";
import PurchaseOrderAddView from "./PurchaseOrderAddView.vue";
import PurchaseOrderEditView from "./PurchaseOrderEditView.vue";
import type {StrictTableColumnsType} from "@/core/antdv/antdev-table";
import {message} from "ant-design-vue";

const transactionApi = inject("transactionApi") as TransactionApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingPurchaseOrder = ref<BasicPurchaseOrder | null>(null);
// 搜索表单
const searchForm = ref({
  no: "",
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
    no: "",
    categoryId: undefined
  };
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

const handleAddPurchaseOrder = () => (showAdd.value = true);
const handleEditPurchaseOrder = (purchaseOrder: BasicPurchaseOrder) => {
  editingPurchaseOrder.value = purchaseOrder;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["purchaseOrders"],
  queryFn: () => transactionApi.listPurchaseOrders()
});

const filteredData = computed(() => {
  const no = searchForm.value.no.trim().toLowerCase();
  if (!no) return data.value;

  return data.value?.filter((purchaseOrder) => {
    return purchaseOrder.orderNo.toLowerCase().includes(no);
  });
});

const { mutate: deletePurchaseOrder } = useMutation({
  mutationFn: (id: number) => transactionApi.deletePurchaseOrder({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["purchaseOrders"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeletePurchaseOrder(id: number) {
  deletePurchaseOrder(id);
}

const columns: StrictTableColumnsType<BasicPurchaseOrder> = [
  { title: "采购单号", dataIndex: "orderNo", key: "orderNo" },
  { title: "供应商名称", dataIndex: "supplierName", key: "supplierName" },
  { title: "订单状态", dataIndex: "status", key: "status" },
  { title: "订单总金额", dataIndex: "totalAmount", key: "totalAmount" },
  { title: "总采购数量", dataIndex: "totalQuantity", key: "totalQuantity" },
  { title: "商品种类数", dataIndex: "itemCount", key: "itemCount" },
  { title: "订单日期", dataIndex: "orderDate", key: "orderDate" },
  { title: "操作", key: "action" }
];
</script>
