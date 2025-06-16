<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item label="用户名" name="name" :rules="[{ required: true, message: '请输入用户名' }]">
      <a-input v-model:value="formState.name" />
    </a-form-item>
    <a-form-item label="姓名" name="nick" :rules="[{ required: true, message: '请输入姓名' }]">
      <a-input v-model:value="formState.nick" />
    </a-form-item>
    <a-form-item label="年龄" name="age" :rules="[]">
      <a-input-number id="inputNumber" v-model:value="formState.age" :min="1" :max="150" />
    </a-form-item>
    <a-form-item label="性别" name="sex" :rules="[]">
      <a-select v-model:value="formState.sex">
        <a-select-option value="male">男</a-select-option>
        <a-select-option value="female">女</a-select-option>
      </a-select>
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
import type { CreateUserInput, UserApi } from "@/core/openapi";
import { useMutation } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<Partial<CreateUserInput>>({
  name: undefined,
  nick: undefined,
  age: undefined,
  sex: undefined
});

const userApi = inject("userApi") as UserApi;

const emit = defineEmits(["success", "update:visible"]);

const { mutate: createUser } = useMutation({
  mutationFn: (newUser: CreateUserInput) => userApi.addUser({ createUserInput: { ...newUser } }),
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
      createUser(formRef.value?.getFieldsValue() as CreateUserInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
