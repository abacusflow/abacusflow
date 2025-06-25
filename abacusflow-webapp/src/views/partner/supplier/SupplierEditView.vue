<template>
  <a-spin :spinning="isPending">
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
  </a-spin>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, watchEffect } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import type { PartnerApi, UpdateSupplierInput } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ supplierId: number }>();

const partnerApi = inject("partnerApi") as PartnerApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateSupplierInput>>({
  name: undefined,
  contactPerson: undefined,
  phone: undefined,
  email: undefined,
  address: undefined
});

// TODO: 当 props.supplierId 变化时，没有重新获取供应商数据，现在是在外层销毁重建了
const {
  data: fetchedSupplier,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["supplier", props.supplierId],
  queryFn: () => partnerApi.getSupplier({ id: props.supplierId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedSupplier.value) {
    const { name, contactPerson, phone, email, address } = fetchedSupplier.value;
    formState.name = name;
    formState.contactPerson = contactPerson;
    formState.phone = phone;
    formState.email = email;
    formState.address = address;
  }
});

const { mutate: updateSupplier } = useMutation({
  mutationFn: (editedSupplier: UpdateSupplierInput) =>
    partnerApi.updateSupplier({ id: props.supplierId, updateSupplierInput: { ...editedSupplier } }),
  onSuccess: () => {
    message.success("修改成功");
    resetForm();
    emit("success"); // 通知父组件修改成功
    closeDrawer();
  },
  onError: (error) => {
    message.error("修改失败");
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
      updateSupplier(formRef.value?.getFieldsValue() as UpdateSupplierInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
