<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>销售单管理</h1>
        <a-button type="primary" @click="handleAddSaleOrder" style="margin-bottom: 16px">
          新增销售单
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="销售单号">
            <a-input v-model:value="searchForm.no" placeholder="请输入销售单号" allow-clear />
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
            <template v-if="column.key === 'orderDate'">
              {{ $dateToFormattedString(record.orderDate, "YYYY-MM-DD") }}
            </template>

            <template v-if="column.key === 'autoCompleteDate'">
              <a-tag :color="getAutoCompleteColor(record.autoCompleteDate)">
                {{
                  record.autoCompleteDate
                    ? `剩 ${dayjs().diff(record.autoCompleteDate, "day") * -1} 天`
                    : "已完成"
                }}
              </a-tag>
            </template>

            <template v-if="column.key === 'status'">
              {{ $translateOrderStatus(record.status) }}
            </template>

            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditSaleOrder(record)"
                  >详情</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定完成该销售单？"
                  @confirm="handleCompleteSaleOrder(record.id)"
                >
                  <a-button type="link" shape="circle">完成订单</a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定取消该销售单？"
                  @confirm="handleCancelSaleOrder(record.id)"
                >
                  <a-button type="link" shape="circle">取消订单</a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定撤回该销售单？"
                  @confirm="handleReverseSaleOrder(record.id)"
                >
                  <a-button type="link" shape="circle">撤回订单</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer
      title="新增销售单"
      width="500"
      :open="showAdd"
      :closable="false"
      @close="showAdd = false"
    >
      <SaleOrderAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer
      title="查看销售单详情"
      width="500"
      :open="showEdit"
      :closable="false"
      @close="showEdit = false"
    >
      <SaleOrderEditView
        v-if="showEdit && editingSaleOrder"
        v-model:visible="showEdit"
        :saleOrderId="editingSaleOrder.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { computed, inject, ref } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { BasicSaleOrder, TransactionApi } from "@/core/openapi";
import SaleOrderAddView from "./SaleOrderAddView.vue";
import SaleOrderEditView from "./SaleOrderDetailView.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import dayjs from "dayjs";

const transactionApi = inject("transactionApi") as TransactionApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingSaleOrder = ref<BasicSaleOrder | null>(null);
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

const handleAddSaleOrder = () => (showAdd.value = true);
const handleEditSaleOrder = (saleOrder: BasicSaleOrder) => {
  editingSaleOrder.value = saleOrder;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["saleOrders"],
  queryFn: () => transactionApi.listSaleOrders()
});

const filteredData = computed(() => {
  const no = searchForm.value.no.trim().toLowerCase();
  if (!no) return data.value;

  return data.value?.filter((saleOrder) => {
    return saleOrder.orderNo.toLowerCase().includes(no);
  });
});

const { mutate: completeSaleOrder } = useMutation({
  mutationFn: (id: number) => transactionApi.completeSaleOrder({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["saleOrders"] });
    message.success("操作成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

const { mutate: cancelSaleOrder } = useMutation({
  mutationFn: (id: number) => transactionApi.cancelSaleOrder({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["saleOrders"] });
    message.success("操作成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

const { mutate: reverseSaleOrder } = useMutation({
  mutationFn: (id: number) => transactionApi.reverseSaleOrder({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["saleOrders"] });
    message.success("操作成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

function handleCompleteSaleOrder(id: number) {
  completeSaleOrder(id);
}

function handleCancelSaleOrder(id: number) {
  cancelSaleOrder(id);
}

function handleReverseSaleOrder(id: number) {
  reverseSaleOrder(id);
}

function getAutoCompleteColor(autoCompleteDate?: string): string {
  if (!autoCompleteDate) return "default";

  const daysLeft = dayjs(autoCompleteDate).diff(dayjs(), "day");

  if (daysLeft < 0) return "red"; // 已超时
  if (daysLeft <= 1) return "orange"; // 紧急
  if (daysLeft <= 3) return "gold"; // 即将到期
  return "green"; // 正常
}

const columns: StrictTableColumnsType<BasicSaleOrder> = [
  { title: "销售单号", dataIndex: "orderNo", key: "orderNo" },
  { title: "客户名称", dataIndex: "customerName", key: "customerName" },
  { title: "订单状态", dataIndex: "status", key: "status" },
  { title: "订单总金额", dataIndex: "totalAmount", key: "totalAmount" },
  { title: "总销售数量", dataIndex: "totalQuantity", key: "totalQuantity" },
  { title: "商品种类数", dataIndex: "itemCount", key: "itemCount" },
  { title: "订单日期", dataIndex: "orderDate", key: "orderDate" },
  { title: "自动完成天数", dataIndex: "autoCompleteDate", key: "autoCompleteDate" },
  { title: "操作", key: "action" }
];
</script>
