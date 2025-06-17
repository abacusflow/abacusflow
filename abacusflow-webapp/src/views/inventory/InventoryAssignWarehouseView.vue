<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" @finish="handleOk">
      <a-form-item
        label="仓库"
        name="warehouseId"
        :rules="[{ required: true, message: '请选择仓库' }]"
      >
        <a-select v-model:value="formState.warehouseId" placeholder="请选择仓库">
          <a-select-option
            v-for="warehouse in warehouses"
            :key="warehouse.id"
            :value="warehouse.id"
          >
            {{ warehouse.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-space>
          <a-button type="primary" html-type="submit">提交</a-button>
          <a-button @click="handleCancel">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-spin>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, watchEffect } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import { type AssignWarehouseRequest, type InventoryApi, WarehouseApi } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ inventoryId: number }>();

const inventoryApi = inject("inventoryApi") as InventoryApi;
const warehouseApi = inject("warehouseApi") as WarehouseApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<AssignWarehouseRequest>>({
  warehouseId: undefined
});

// TODO: 当 props.inventoryId 变化时，没有重新获取库存数据，现在是在外层销毁重建了
const {
  data: fetchedInventory,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["inventory", props.inventoryId],
  queryFn: () => inventoryApi.getInventory({ id: props.inventoryId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedInventory.value) {
    const { warehouseId } = fetchedInventory.value;
    formState.warehouseId = warehouseId || 1;
  }
});

const { data: warehouses } = useQuery({
  queryKey: ["warehouses"],
  queryFn: () => warehouseApi.listWarehouses()
});

const { mutate: assignWarehouse } = useMutation({
  mutationFn: ({ warehouseId }: AssignWarehouseRequest) =>
    inventoryApi.assignWarehouse({
      id: props.inventoryId,
      assignWarehouseRequest: { warehouseId }
    }),
  onSuccess: () => {
    message.success("修改成功");
    resetForm();
    emit("success"); // 通知父组件修改成功
    closeDrawer();
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

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
      assignWarehouse(formRef.value?.getFieldsValue() as { id: number } & AssignWarehouseRequest);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
