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
          <a-form-item label="采购单号">
            <a-input v-model:value="searchForm.orderNo" placeholder="请输入采购单号" allow-clear />
          </a-form-item>
          <a-form-item label="客户名">
            <a-input
              v-model:value="searchForm.customerName"
              placeholder="请输入客户名"
              allow-clear
            />
          </a-form-item>
          <a-form-item label="产品名">
            <a-input
              v-model:value="searchForm.productName"
              placeholder="请输入产品名"
              allow-clear
            />
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
          :data-source="pageData?.content || []"
          :loading="isPending"
          row-key="id"
          size="small"
          :pagination="pagination"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tooltip :title="statusTooltip(record.status)">
                <a-tag :color="statusColor(record.status)">
                  {{ $translateOrderStatus(record.status) }}
                </a-tag>
              </a-tooltip>
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

            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditSaleOrder(record)"
                  >详情</a-button
                >

                <a-divider type="vertical" />

                <!-- 完成订单按钮 -->
                <a-popconfirm
                  title="确定完成该销售单？"
                  @confirm="handleCompleteSaleOrder(record.id)"
                  :disabled="record.status !== OrderStatus.pending"
                >
                  <a-button
                    type="link"
                    shape="circle"
                    :disabled="record.status !== OrderStatus.pending"
                  >
                    完成订单
                  </a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <!-- 取消订单按钮 -->
                <a-popconfirm
                  title="确定取消该销售单？"
                  @confirm="handleCancelSaleOrder(record.id)"
                  :disabled="record.status !== OrderStatus.pending"
                >
                  <a-button
                    type="link"
                    shape="circle"
                    :disabled="record.status !== OrderStatus.pending"
                  >
                    取消订单
                  </a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <!-- 撤回订单按钮 -->
                <a-popconfirm
                  title="确定撤回该销售单？"
                  @confirm="handleReverseSaleOrder(record.id)"
                  :disabled="
                    record.status !== OrderStatus.completed &&
                    record.status !== OrderStatus.canceled
                  "
                >
                  <a-button
                    type="link"
                    shape="circle"
                    :disabled="
                      record.status !== OrderStatus.completed &&
                      record.status !== OrderStatus.canceled
                    "
                  >
                    撤回订单
                  </a-button>
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
import { computed, inject, reactive, ref } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import {
  OrderStatus,
  type BasicSaleOrder,
  type ListSaleOrdersPageRequest,
  type TransactionApi
} from "@/core/openapi";
import SaleOrderAddView from "./SaleOrderAddView.vue";
import SaleOrderEditView from "./SaleOrderDetailView.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import dayjs from "dayjs";
import { dateToFormattedString } from "@/util/timestampUtils";

const transactionApi = inject("transactionApi") as TransactionApi;
const queryClient = useQueryClient();

const pageIndex = ref(1);
const pageSize = ref(10);
const showAdd = ref(false);
const showEdit = ref(false);
const editingSaleOrder = ref<BasicSaleOrder | null>(null);
// 搜索表单
const searchForm = reactive({
  orderNo: undefined,
  status: undefined,
  productName: undefined,
  customerName: undefined
});

const pagination = computed(() => ({
  current: pageIndex.value,
  pageSize: pageSize.value,
  total: pageData.value?.totalElements,
  showTotal: (total: number) => `共 ${total} 条`,
  onChange: (page: number, size: number) => {
    pageIndex.value = page;
    pageSize.value = size;
    refetch();
  }
}));

// 搜索
const handleSearch = () => {
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.orderNo = undefined;
  searchForm.status = undefined;
  searchForm.productName = undefined;
  searchForm.customerName = undefined;

  refetch();
};

const handleAddSaleOrder = () => (showAdd.value = true);
const handleEditSaleOrder = (saleOrder: BasicSaleOrder) => {
  editingSaleOrder.value = saleOrder;
  showEdit.value = true;
};

const {
  data: pageData,
  isPending,
  refetch
} = useQuery({
  queryKey: [
    "saleOrders",
    searchForm.orderNo,
    searchForm.customerName,
    searchForm.status,
    searchForm.productName,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { orderNo, customerName, status, productName } = searchForm;
    const params: ListSaleOrdersPageRequest = {
      orderNo: orderNo || undefined,
      customerName: customerName || undefined,
      status: status || undefined,
      productName: productName || undefined,
      pageIndex: pageIndex.value,
      pageSize: pageSize.value
    };
    return transactionApi.listSaleOrdersPage(params);
  }
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

// 获取状态的提示信息
function statusTooltip(status: OrderStatus): string {
  switch (status) {
    case OrderStatus.pending:
      return "订单待处理，请尽快处理"; // 提示
    case OrderStatus.completed:
      return "订单已完成，感谢您的处理"; // 提示
    case OrderStatus.canceled:
      return "订单已取消，无法处理"; // 提示
    case OrderStatus.reversed:
      return "订单已回退，无法继续处理"; // 提示
    default:
      return "未知状态"; // 提示
  }
}

// 获取状态的标签颜色
function statusColor(status: OrderStatus): string {
  switch (status) {
    case OrderStatus.pending:
      return "yellow"; // 黄色
    case OrderStatus.completed:
      return "green"; // 绿色
    case OrderStatus.canceled:
      return "red"; // 红色
    case OrderStatus.reversed:
      return "orange"; // 橙色（回退状态使用橙色）
    default:
      return "blue"; // 默认蓝色
  }
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
  {
    title: "订单总金额",
    dataIndex: "totalAmount",
    key: "totalAmount",
    customRender: ({ text }) => text.toFixed(2)
  },
  {
    title: "总销售数量",
    dataIndex: "totalQuantity",
    key: "totalQuantity"
  },
  { title: "商品种类数", dataIndex: "itemCount", key: "itemCount" },
  {
    title: "订单日期",
    dataIndex: "orderDate",
    key: "orderDate",
    customRender: ({ record }) => dateToFormattedString(record.orderDate, "YYYY-MM-DD")
  },
  { title: "自动完成天数", dataIndex: "autoCompleteDate", key: "autoCompleteDate" },
  { title: "操作", key: "action" }
];
</script>
