<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item
      label="供应商名"
      name="name"
      :rules="[{ required: true, message: '请输入供应商名' }]"
    >
      <a-input v-model:value="formState.name" />
    </a-form-item>

    <a-form-item label="联系人" name="contactPerson">
      <a-input v-model:value="formState.contactPerson" />
    </a-form-item>

    <a-form-item
      label="联系电话"
      name="phone"
      :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }]"
    >
      <a-input v-model:value="formState.phone" />
    </a-form-item>

    <a-form-item
      label="电子邮箱"
      name="email"
      :rules="[{ type: 'email', message: '请输入有效的电子邮箱' }]"
    >
      <a-input v-model:value="formState.email" />
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
import { ref, reactive } from "vue";
import { message, type FormInstance } from "ant-design-vue";
import { inject } from "vue";
import type { CreateSupplierInput, PartnerApi } from "@/core/openapi";
import { useMutation } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<CreateSupplierInput>({
  name: "",
  contactPerson: undefined,
  phone: undefined,
  email: undefined,
  address: undefined
});

const partnerApi = inject("partnerApi") as PartnerApi;

const emit = defineEmits(["success", "update:visible"]);

const { mutate: createSupplier } = useMutation({
  mutationFn: (newSupplier: CreateSupplierInput) =>
    partnerApi.addSupplier({ createSupplierInput: { ...newSupplier } }),
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
      createSupplier(formRef.value?.getFieldsValue() as CreateSupplierInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
