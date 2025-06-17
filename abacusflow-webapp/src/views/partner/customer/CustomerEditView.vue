<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" @finish="handleOk">
      <a-form-item
        label="客户名"
        name="name"
        :rules="[{ required: true, message: '请输入客户名' }]"
      >
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
  </a-spin>
</template>

<script lang="ts" setup>
import {inject, reactive, ref, watchEffect} from "vue";
import {type FormInstance, message} from "ant-design-vue";
import type {PartnerApi, UpdateCustomerInput} from "@/core/openapi";
import {useMutation, useQuery} from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ customerId: number }>();

const partnerApi = inject("partnerApi") as PartnerApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateCustomerInput>>({
  name: undefined,
  phone: undefined,
  address: undefined
});

// TODO: 当 props.customerId 变化时，没有重新获取客户数据，现在是在外层销毁重建了
const {
  data: fetchedCustomer,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["customer", props.customerId],
  queryFn: () => partnerApi.getCustomer({ id: props.customerId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedCustomer.value) {
    const { name, phone, address } = fetchedCustomer.value;
    formState.name = name;
    formState.phone = phone;
    formState.address = address;
  }
});

const { mutate: updateCustomer } = useMutation({
  mutationFn: (editedCustomer: UpdateCustomerInput) =>
    partnerApi.updateCustomer({ id: props.customerId, updateCustomerInput: { ...editedCustomer } }),
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
      updateCustomer(formRef.value?.getFieldsValue() as UpdateCustomerInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
