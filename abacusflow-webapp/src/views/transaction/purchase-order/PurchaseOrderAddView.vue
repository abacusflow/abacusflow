<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
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
      <a-date-picker v-model:value="formState.orderDate" :format="dateFormat" style="width: 100%" />
    </a-form-item>

    <a-form-item label="订单明细" required>
      <div
        v-for="(item, index) in formState.orderItems"
        :key="index"
        style="margin-bottom: 12px; border: 1px dashed #ccc; padding: 12px; border-radius: 6px"
      >
        <!-- 商品名称 -->
        <a-form-item
          label="商品名称"
          :name="['orderItems', index, 'productId']"
          :rules="[{ required: true, message: '请选择产品' }]"
        >
          <a-select v-model:value="item.productId" placeholder="请选择产品">
            <a-select-option v-for="product in products" :key="product.id" :value="product.id">
              {{ product.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <!-- 数量 -->
        <a-form-item
          label="数量"
          :name="['orderItems', index, 'quantity']"
          :rules="[{ required: true, message: '请输入数量' }]"
        >
          <a-tooltip
            v-if="isDisabledQuantity(item.productId, products!)"
            title="该产品为资产类，数量不可修改"
          >
            <a-input-number
              v-model:value="item.quantity"
              :disabled="true"
              :min="1"
              placeholder="数量"
              style="width: 100%"
            />
          </a-tooltip>

          <a-input-number
            v-else
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

        <!-- 序列号 -->
        <a-form-item label="序列号" :name="['orderItems', index, 'serialNumber']">
          <a-input v-model:value="item.serialNumber" />
        </a-form-item>

        <!-- 删除按钮 -->
        <a-button danger type="link" @click="removeOrderItem(index)"> 删除该商品 </a-button>
      </div>

      <!-- 添加一项 -->
      <a-button type="dashed" block @click="addOrderItem"> 添加商品明细 </a-button>
    </a-form-item>

    <a-form-item label="备注" name="note">
      <a-textarea v-model:value="formState.note" placeholder="请输入备注" />
    </a-form-item>

    <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
      <a-space>
        <a-button type="primary" html-type="submit">提交</a-button>
        <a-button @click="handleCancel">取消</a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
import { inject, reactive, ref } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import {
  ProductType,
  type BasicProduct,
  type CreatePurchaseOrderInput,
  type PartnerApi,
  type ProductApi,
  type PurchaseOrderItemInput,
  type TransactionApi
} from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";
import dayjs, { Dayjs } from "dayjs";

const formRef = ref<FormInstance>();
const dateFormat = "YYYY/MM/DD";
type CreatePurchaseOrderInputForm = Omit<CreatePurchaseOrderInput, "orderDate" | "orderItems"> & {
  orderDate: Dayjs;
  orderItems: Partial<PurchaseOrderItemInput>[];
};

const formState = reactive<Partial<CreatePurchaseOrderInputForm>>({
  supplierId: undefined,
  orderDate: dayjs(dayjs().format(dateFormat), dateFormat),
  note: undefined,
  orderItems: []
});

const transactionApi = inject("transactionApi") as TransactionApi;
const partnerApi = inject("partnerApi") as PartnerApi;
const productApi = inject("productApi") as ProductApi;

const emit = defineEmits(["success", "update:visible"]);

const { data: suppliers } = useQuery({
  queryKey: ["suppliers"],
  queryFn: () => partnerApi.listSuppliers()
});

const { data: products } = useQuery({
  queryKey: ["products"],
  queryFn: () => productApi.listProducts()
});

const { mutate: createPurchaseOrder } = useMutation({
  mutationFn: (newPurchaseOrder: CreatePurchaseOrderInput) =>
    transactionApi.addPurchaseOrder({ createPurchaseOrderInput: { ...newPurchaseOrder } }),
  onSuccess: () => {
    message.success("添加成功");
    resetForm();
    emit("success"); // 通知父组件添加成功
    closeDrawer();
  },
  onError: (error) => {
    message.error("添加失败");
    console.error(error);
  }
});

function addOrderItem() {
  formState.orderItems?.push({
    productId: undefined,
    quantity: 1,
    unitPrice: 0,
    serialNumber: undefined
  });
}

function removeOrderItem(index: number) {
  formState.orderItems?.splice(index, 1);
}

function isDisabledQuantity(productId?: number, products?: BasicProduct[]): boolean {
  if (!productId) return false;
  if (!products) return false;

  const product = products.find((p) => p.id === productId);
  return product?.type === ProductType.Asset;
}

const handleCancel = () => {
  resetForm();

  closeDrawer();
};

const closeDrawer = () => {
  emit("update:visible", false); // 触发 v-model:visible 改变
};

const resetForm = () => {
  formRef.value?.resetFields();
};

const handleOk = () => {
  formRef.value
    ?.validate()
    .then(() => {
      createPurchaseOrder(formRef.value?.getFieldsValue() as CreatePurchaseOrderInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
