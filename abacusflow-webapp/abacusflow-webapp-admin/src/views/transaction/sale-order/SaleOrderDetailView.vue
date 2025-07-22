<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" disabled>
      <a-form-item
        label="客户"
        name="customerId"
        :rules="[{ required: true, message: '请选择客户' }]"
      >
        <a-select v-model:value="formState.customerId" placeholder="请选择客户">
          <a-select-option v-for="customer in customers" :key="customer.id" :value="customer.id">
            {{ customer.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="订单日期"
        name="orderDate"
        :rules="[{ required: true, message: '请选择订单日期' }]"
      >
        <a-date-picker
          v-model:value="formState.orderDate"
          :format="dateFormat"
          style="width: 100%"
        />
      </a-form-item>

      <a-form-item label="订单明细" required>
        <a-list :data-source="formState.orderItems" bordered size="small">
          <template #renderItem="{ item }">
            <a-list-item style="padding: 0">
              <a-card size="small" :bordered="false" style="width: 100%">
                <a-row>
                  <strong>库存产品：</strong>
                  <span>{{ findInventoryTitle(item.inventoryUnitId) }} </span>
                </a-row>
                <a-row>
                  <strong>数量：</strong>
                  <span>{{ item.quantity }}</span>
                </a-row>
                <a-row>
                  <strong>单价：</strong>
                  <span>¥{{ item.unitPrice?.toFixed(2) }}</span>
                </a-row>
                <a-row>
                  <strong>折后单价：</strong>
                  <span>¥{{ item.discountedPrice?.toFixed(2) }}</span>
                </a-row>
              </a-card>
            </a-list-item>
          </template>
        </a-list>
      </a-form-item>

      <a-form-item label="备注" name="note">
        <a-textarea v-model:value="formState.note" />
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, watchEffect } from "vue";
import { type FormInstance } from "ant-design-vue";
import {
  InventoryApi,
  type ListSelectableInventoryUnitsRequest,
  type PartnerApi,
  type SaleOrder,
  type TransactionApi
} from "@/core/openapi";
import { useQuery } from "@tanstack/vue-query";
import dayjs, { Dayjs } from "dayjs";

const formRef = ref<FormInstance>();
const dateFormat = "YYYY/MM/DD";
type SaleOrderForm = Omit<SaleOrder, "orderDate"> & {
  orderDate: Dayjs;
};

const props = defineProps<{ saleOrderId: number }>();

const transactionApi = inject("transactionApi") as TransactionApi;
const partnerApi = inject("partnerApi") as PartnerApi;
const inventoryApi = inject("inventoryApi") as InventoryApi;

const formState = reactive<Partial<SaleOrderForm>>({
  customerId: undefined,
  orderDate: undefined,
  note: undefined,
  orderItems: []
});

// TODO: 当 props.saleOrderId 变化时，没有重新获取销售单数据，现在是在外层销毁重建了
const {
  data: fetchedSaleOrder,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["saleOrder", props.saleOrderId],
  queryFn: () => transactionApi.getSaleOrder({ id: props.saleOrderId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedSaleOrder.value) {
    const { customerId, orderDate, note, orderItems } = fetchedSaleOrder.value;
    formState.customerId = customerId;
    formState.orderDate = dayjs(orderDate);
    formState.note = note;
    formState.orderItems = reactive([...orderItems]);
  }
});

const { data: customers } = useQuery({
  queryKey: ["customers"],
  queryFn: () => partnerApi.listSelectableCustomers()
});

const { data: selectableInventoryUnits } = useQuery({
  queryKey: ["selectableInventoryUnits"],
  queryFn: () => {
    const params: ListSelectableInventoryUnitsRequest = {};
    return inventoryApi.listSelectableInventoryUnits(params);
  }
});

function findInventoryTitle(inventoryUnitId?: number): string {
  const unit = selectableInventoryUnits?.value?.find((u) => u.id === inventoryUnitId);
  return unit?.title || "-";
}
</script>
