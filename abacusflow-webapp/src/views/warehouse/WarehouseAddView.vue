<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item label="仓库名" name="name" :rules="[{ required: true, message: '请输入仓库名' }]">
      <a-input v-model:value="formState.name" />
    </a-form-item>

    <a-form-item label="仓库地址" name="location">
      <a-input v-model:value="formState.location" />
    </a-form-item>

    <a-form-item label="仓库容量" name="capacity">
      <a-input v-model:value="formState.capacity" />
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
import { ref, reactive } from "vue";
import { message, type FormInstance } from "ant-design-vue";
import { inject } from "vue";
import { WarehouseApi, type CreateWarehouseInput } from "@/core/openapi";
import { useMutation } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<CreateWarehouseInput>({
  name: "",
  location: undefined,
  capacity: undefined
});

const warehouseApi = inject("warehouseApi") as WarehouseApi;

const emit = defineEmits(["success", "update:visible"]);

const { mutate: createWarehouse } = useMutation({
  mutationFn: (newWarehouse: CreateWarehouseInput) =>
    warehouseApi.addWarehouse({ createWarehouseInput: { ...newWarehouse } }),
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
      createWarehouse(formRef.value?.getFieldsValue() as CreateWarehouseInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
