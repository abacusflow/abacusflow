<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" @finish="handleOk">
      <a-form-item
        label="安全库存预警线"
        name="safetyStock"
        :rules="[{ required: true, message: '请输入安全库存预警线' }]"
      >
        <a-input-number id="safetyStock" v-model:value="formState.safetyStock" :min="1" />
      </a-form-item>

      <a-form-item
        label="最高库存预警线"
        name="maxStock"
        :rules="[{ required: true, message: '请输入最高库存预警线' }]"
      >
        <a-input-number id="maxStock" v-model:value="formState.maxStock" :min="1" />
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
import {inject, reactive, ref, watchEffect} from "vue";
import {type FormInstance, message} from "ant-design-vue";
import type {AdjustWarningLineRequest, InventoryApi} from "@/core/openapi";
import {useMutation, useQuery} from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ inventoryId: number }>();

const inventoryApi = inject("inventoryApi") as InventoryApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<AdjustWarningLineRequest>>({
  safetyStock: undefined,
  maxStock: undefined
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
    const { safetyStock, maxStock } = fetchedInventory.value;
    formState.safetyStock = safetyStock || 1;
    formState.maxStock = maxStock || 10;
  }
});

const { mutate: adjustWarningLine } = useMutation({
  mutationFn: ({ safetyStock, maxStock }: AdjustWarningLineRequest) =>
    inventoryApi.adjustWarningLine({
      id: props.inventoryId,
      adjustWarningLineRequest: { safetyStock, maxStock }
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
      adjustWarningLine(
        formRef.value?.getFieldsValue() as { id: number } & AdjustWarningLineRequest
      );
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
