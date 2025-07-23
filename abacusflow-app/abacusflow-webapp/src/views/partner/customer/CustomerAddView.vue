<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item label="客户名" name="name" :rules="[{ required: true, message: '请输入客户名' }]">
      <a-input v-model:value="formState.name" />
    </a-form-item>

    <a-form-item
      label="联系方式"
      name="phone"
      :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }]"
    >
      <a-input v-model:value="formState.phone" />
    </a-form-item>

    <a-form-item label="地址" name="address">
      <a-input v-model:value="formState.address" />
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
import type { CreateCustomerInput, PartnerApi } from "@/core/openapi";
import { useMutation } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<Partial<CreateCustomerInput>>({
  name: undefined,
  phone: undefined,
  address: undefined
});

const partnerApi = inject("partnerApi") as PartnerApi;

const emit = defineEmits(["success", "update:visible"]);

const { mutate: createCustomer } = useMutation({
  mutationFn: (newCustomer: CreateCustomerInput) =>
    partnerApi.addCustomer({ createCustomerInput: { ...newCustomer } }),
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
      createCustomer(formRef.value?.getFieldsValue() as CreateCustomerInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
