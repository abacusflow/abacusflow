<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" disabled>
      <a-form-item
        label="客户"
        name="customerId"
        :rules="[{ required: true, message: '请选择客户' }]"
      >
        <a-select v-model:value="formState.customerId" placeholder="请选择客户">
          <a-select-option
            v-for="customer in customers"
            :key="customer.value"
            :value="customer.value"
          >
            {{ customer.label }}
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
        <div
          v-for="(item, index) in formState.orderItems"
          :key="index"
          style="margin-bottom: 12px; border: 1px dashed #ccc; padding: 12px; border-radius: 6px"
        >
          <!-- 产品名称 -->
          <a-form-item
            label="库存产品"
            :name="['orderItems', index, 'inventoryUnitId']"
            :rules="[{ required: true, message: '请选择库存产品' }]"
          >
            <a-select v-model:value="item.inventoryUnitId" placeholder="请选择库存产品">
              <a-select-option
                v-for="inventoryUnit in selectableInventoryUnits"
                :key="inventoryUnit.id"
                :value="inventoryUnit.id"
              >
                {{ inventoryUnit.title }}
              </a-select-option>
            </a-select>
          </a-form-item>

          <!-- 数量 -->
          <a-form-item
            label="数量"
            :name="['orderItems', index, 'quantity']"
            :rules="[{ required: true, message: '请输入数量' }]"
          >
            <a-input-number
              v-model:value="item.quantity"
              :min="1"
              placeholder="数量"
              style="width: 100%"
            />
          </a-form-item>

          <!-- 单价 -->
          <a-form-item
            label="单价"
            :name="['orderItems', index, 'unitPrice']"
            :rules="[{ required: true, message: '请输入单价' }]"
          >
            <a-input-number
              v-model:value="item.unitPrice"
              placeholder="单价"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
          </a-form-item>

          <!-- 折后单价 -->
          <a-form-item
            label="折后单价"
            :name="['orderItems', index, 'discountedPrice']"
            :rules="[{ required: true, message: '请输入折后单价' }]"
          >
            <a-input-number
              v-model:value="item.discountedPrice"
              placeholder="折后单价"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
          </a-form-item>
        </div>
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
import { InventoryApi, type PartnerApi, type SaleOrder, type TransactionApi } from "@/core/openapi";
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
  queryFn: () => inventoryApi.listSelectableInventoryUnits()
});
</script>
