<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item
      label="客户"
      name="customerId"
      :rules="[{ required: true, message: '请选择客户' }]"
    >
      <a-select
        v-model:value="formState.customerId"
        show-search
        placeholder="请选择客户"
        optionFilterProp="label"
      >
        <a-select-option
          v-for="customer in customers"
          :key="customer.id"
          :value="customer.id"
          :label="customer.name"
        >
          {{ customer.name }}
        </a-select-option>

        <template #dropdownRender="{ menuNode }">
          <div>
            <!-- 默认菜单 -->
            <component :is="menuNode" />

            <a-divider style="margin: 4px 0" />

            <!-- 自定义新增项 -->
            <a-flex justify="center" align="center">
              <a-button type="link" :icon="h(PlusOutlined)" block @click="showAddCustomer = true"
                >添加客户</a-button
              >
            </a-flex>
          </div>
        </template>
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
        <!-- 产品名称 -->
        <a-form-item
          label="库存产品"
          :name="['orderItems', index, 'inventoryUnitId']"
          :rules="[{ required: true, message: '请选择库存产品' }]"
        >
          <a-select
            v-model:value="item.inventoryUnitId"
            show-search
            placeholder="请选择库存产品"
            optionFilterProp="label"
          >
            <a-select-option
              v-for="inventoryUnit in selectableInventoryUnits"
              :key="inventoryUnit.id"
              :value="inventoryUnit.id"
              :label="inventoryUnit.title"
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
          <a-tooltip
            v-if="isInstance(item.inventoryUnitId, selectableInventoryUnits!)"
            title="该库存单元属于实例，数量不可修改"
          >
            <a-input-number
              :value="1"
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

        <a-form-item
          label="折扣率"
          :name="['orderItems', index, 'discountFactor']"
          :rules="[{ required: true, message: '请输入折扣率' }]"
        >
          <a-flex justify="flex-start" align="center" style="height: 100%">
            <!-- 折扣率输入框 -->
            <a-input-number
              v-model:value="item.discountFactor"
              addon-after="%"
              placeholder="请输入折扣率"
              :min="1"
              :max="100"
              style="width: 100%"
            />

            <span style="display: inline-block; margin-left: 10px"
              >折扣价: {{ calculateDiscountedPrice(item.unitPrice, item.discountFactor) }}</span
            >
          </a-flex>
        </a-form-item>

        <!-- 删除按钮 -->
        <a-button danger type="link" @click="removeOrderItem(index)"> 删除该产品 </a-button>
      </div>

      <!-- 添加一项 -->
      <a-button type="dashed" block @click="addOrderItem"> 添加产品明细 </a-button>
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

  <a-drawer
    title="新增客户"
    :open="showAddCustomer"
    :closable="false"
    @close="showAddCustomer = false"
  >
    <CustomerAddView
      v-if="showAddCustomer"
      v-model:visible="showAddCustomer"
      @success="refetchCustomers"
    />
  </a-drawer>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, h } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import { PlusOutlined } from "@ant-design/icons-vue";
import {
  type CreateSaleOrderInput,
  InventoryApi,
  InventoryUnitType,
  type ListSelectableInventoryUnitsRequest,
  type PartnerApi,
  type SaleOrderItemInput,
  type SelectableInventoryUnit,
  type TransactionApi
} from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";
import dayjs, { Dayjs } from "dayjs";
import CustomerAddView from "@/views/partner/customer/CustomerAddView.vue";

const formRef = ref<FormInstance>();
const showAddCustomer = ref(false);

const dateFormat = "YYYY/MM/DD";
type CreateSaleOrderInputForm = Omit<CreateSaleOrderInput, "orderDate" | "orderItems"> & {
  orderDate: Dayjs;
  orderItems: Partial<SaleOrderItemInput>[];
};

const formState = reactive<Partial<CreateSaleOrderInputForm>>({
  customerId: undefined,
  orderDate: dayjs(dayjs().format(dateFormat), dateFormat),
  note: undefined,
  orderItems: []
});

const transactionApi = inject("transactionApi") as TransactionApi;
const partnerApi = inject("partnerApi") as PartnerApi;
const inventoryApi = inject("inventoryApi") as InventoryApi;

const emit = defineEmits(["success", "update:visible"]);

const { data: customers, refetch: refetchCustomers } = useQuery({
  queryKey: ["customers"],
  queryFn: () => partnerApi.listSelectableCustomers()
});

const { data: selectableInventoryUnits } = useQuery({
  queryKey: ["selectableInventoryUnits"],
  queryFn: () => {
    const params: ListSelectableInventoryUnitsRequest = {
      statuses: ["normal", "reversed"]
    };
    return inventoryApi.listSelectableInventoryUnits(params);
  }
});

const { mutate: createSaleOrder } = useMutation({
  mutationFn: (newSaleOrder: CreateSaleOrderInput) =>
    transactionApi.addSaleOrder({ createSaleOrderInput: { ...newSaleOrder } }),
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
    inventoryUnitId: undefined,
    quantity: 1,
    unitPrice: undefined,
    discountFactor: 100
  });
}

function removeOrderItem(index: number) {
  formState.orderItems?.splice(index, 1);
}

function isInstance(inventoryUnitId?: number, inventoryUnits?: SelectableInventoryUnit[]): boolean {
  if (!inventoryUnitId) return false;
  if (!inventoryUnits) return false;

  const inventoryUnit = inventoryUnits.find((p) => p.id === inventoryUnitId);
  return inventoryUnit?.type === InventoryUnitType.instance;
}

function calculateDiscountedPrice(unitPrice?: number, discountFactor?: number) {
  if (!unitPrice) return 0;
  if (!discountFactor) return unitPrice;
  const discountedPrice = unitPrice * (discountFactor / 100);

  return parseFloat(discountedPrice.toFixed(2));
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
      const formData = formRef.value?.getFieldsValue() as CreateSaleOrderInput;

      // 创建一个新的对象并转换 discountFactor 为 0.01 - 1.00 范围
      const transformedFormData = {
        ...formData,
        orderItems: formData.orderItems.map((item) => ({
          ...item,
          discountFactor: item.discountFactor ? item.discountFactor / 100 : 1.0
        }))
      };

      createSaleOrder(transformedFormData);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
