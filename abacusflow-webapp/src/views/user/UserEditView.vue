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
import { ref, computed } from "vue";
import { message, type FormInstance } from "ant-design-vue";
import { inject } from "vue";
import type { UpdateUserInput, UserApi } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ userId: number }>();

const userApi = inject("userApi") as UserApi;

const emit = defineEmits(["success", "close", "update:visible"]);

// TODO: 当 props.userId 变化时，没有重新获取用户数据，现在是在外层销毁重建了
const {
  data: fetchedUser,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["user", props.userId],
  queryFn: () => userApi.getUser({ id: props.userId })
});

const formState = computed<UpdateUserInput>(() => {
  // 当查询成功且有数据时，优先使用 API 数据
  if (isSuccess.value && fetchedUser.value) {
    const { name, nick, age, sex } = fetchedUser.value;
    return { name, nick, age, sex };
  }

  // 没有 API 数据时，返回基础状态
  return {
    name: "",
    nick: "",
    age: undefined,
    sex: undefined
  };
});

const { mutate: updateUser } = useMutation({
  mutationFn: (editedUser: UpdateUserInput) =>
    userApi.updateUser({ id: props.userId, updateUserInput: { ...editedUser } }),
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
      // console.log("表单验证通过，提交数据", formRef.value?.getFieldsValue());
      updateUser(formRef.value?.getFieldsValue() as UpdateUserInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
