<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item
      label="储存点名"
      name="name"
      :rules="[{ required: true, message: '请输入储存点名' }]"
    >
      <a-input v-model:value="formState.name" />
    </a-form-item>

    <a-form-item label="储存点地址" name="location">
      <a-input v-model:value="formState.location" />
    </a-form-item>

    <a-form-item label="储存点容量" name="capacity">
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
import { inject, reactive, ref } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import { type CreateDepotInput, DepotApi } from "@/core/openapi";
import { useMutation } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<Partial<CreateDepotInput>>({
  name: undefined,
  location: undefined,
  capacity: undefined
});

const depotApi = inject("depotApi") as DepotApi;

const emit = defineEmits(["success", "update:visible"]);

const { mutate: createDepot } = useMutation({
  mutationFn: (newDepot: CreateDepotInput) =>
    depotApi.addDepot({ createDepotInput: { ...newDepot } }),
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
      createDepot(formRef.value?.getFieldsValue() as CreateDepotInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
