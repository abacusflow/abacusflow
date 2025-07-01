<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" disabled>
      <a-form-item
        label="供应商"
        name="supplierId"
        :rules="[{ required: true, message: '请选择供应商' }]"
      >
        <a-select v-model:value="formState.supplierId" placeholder="请选择供应商">
          <a-select-option v-for="supplier in suppliers" :key="supplier.id" :value="supplier.id">
            {{ supplier.name }}
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

      <a-form-item label="订单明细">
        <a-list :data-source="formState.orderItems" bordered size="small">
          <template #renderItem="{ item }">
            <a-list-item style="padding: 0;">
              <a-card size="small" :bordered="false" style="width: 100%">
                <a-row><strong>产品名称：</strong> {{ findProductName(item.productId) }}</a-row>
                <a-row><strong>数量：</strong> {{ item.quantity }}</a-row>
                <a-row><strong>单价：</strong> ¥{{ item.unitPrice?.toFixed(2) }}</a-row>
                <a-row v-if="isAsset(item.productId, products)">
                  <strong>序列号：</strong> {{ item.serialNumber || "-" }}
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
  type PartnerApi,
  type ProductApi,
  ProductType,
  type PurchaseOrder,
  type SelectableProduct,
  type TransactionApi
} from "@/core/openapi";
import { useQuery } from "@tanstack/vue-query";
import dayjs, { Dayjs } from "dayjs";

const dateFormat = "YYYY/MM/DD";
const formRef = ref<FormInstance>();

const props = defineProps<{ purchaseOrderId: number }>();

const transactionApi = inject("transactionApi") as TransactionApi;
const productApi = inject("productApi") as ProductApi;
const partnerApi = inject("partnerApi") as PartnerApi;

type PurchaseOrderForm = Omit<PurchaseOrder, "orderDate"> & {
  orderDate: Dayjs;
};
const formState = reactive<Partial<PurchaseOrderForm>>({
  supplierId: undefined,
  orderDate: dayjs(dayjs().format(dateFormat), dateFormat),
  note: undefined,
  orderItems: []
});

// TODO: 当 props.purchaseOrderId 变化时，没有重新获取采购单数据，现在是在外层销毁重建了
const {
  data: fetchedPurchaseOrder,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["purchaseOrder", props.purchaseOrderId],
  queryFn: () => transactionApi.getPurchaseOrder({ id: props.purchaseOrderId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedPurchaseOrder.value) {
    const { supplierId, orderDate, note, orderItems } = fetchedPurchaseOrder.value;
    formState.supplierId = supplierId;
    formState.orderDate = dayjs(orderDate);
    formState.note = note;
    formState.orderItems = reactive([...orderItems]);
  }
});

const { data: suppliers } = useQuery({
  queryKey: ["suppliers"],
  queryFn: () => partnerApi.listSelectableSuppliers()
});

const { data: products } = useQuery({
  queryKey: ["products"],
  queryFn: () => productApi.listSelectableProducts()
});

function isAsset(productId?: number, products?: SelectableProduct[]): boolean {
  if (!productId) return false;
  if (!products) return false;

  const product = products.find((p) => p.id === productId);
  return product?.type === ProductType.Asset;
}

function findProductName(productId?: number): string {
  const product = products?.value?.find((p) => p.id === productId);
  return product?.name || "-";
}
</script>
