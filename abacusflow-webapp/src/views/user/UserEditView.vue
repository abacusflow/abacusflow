<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" @finish="handleOk">
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
  </a-spin>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, watchEffect } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import type { UpdateUserInput, UserApi } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ userId: number }>();

const userApi = inject("userApi") as UserApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateUserInput>>({
  nick: undefined,
  age: undefined,
  sex: undefined
});

// TODO: 当 props.userId 变化时，没有重新获取用户数据，现在是在外层销毁重建了
const {
  data: fetchedUser,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["user", props.userId],
  queryFn: () => userApi.getUser({ id: props.userId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedUser.value) {
    const { nick, age, sex } = fetchedUser.value;
    formState.nick = nick;
    formState.age = age;
    formState.sex = sex;
  }
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
      updateUser(formRef.value?.getFieldsValue() as UpdateUserInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
