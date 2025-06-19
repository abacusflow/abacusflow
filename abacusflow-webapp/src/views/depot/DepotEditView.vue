<template>
  <a-spin :spinning="isPending">
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
  </a-spin>
</template>

<script lang="ts" setup>
import { inject, reactive, ref, watchEffect } from "vue";
import { type FormInstance, message } from "ant-design-vue";
import { type UpdateDepotInput, type DepotApi } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ depotId: number }>();

const depotApi = inject("depotApi") as DepotApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateDepotInput>>({
  name: undefined,
  location: undefined,
  capacity: undefined
});

// TODO: 当 props.depotId 变化时，没有重新获取储存点数据，现在是在外层销毁重建了
const {
  data: fetchedDepot,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["depot", props.depotId],
  queryFn: () => depotApi.getDepot({ id: props.depotId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedDepot.value) {
    const { name, location, capacity } = fetchedDepot.value;
    formState.name = name;
    formState.location = location;
    formState.capacity = capacity;
  }
});

const { mutate: updateDepot } = useMutation({
  mutationFn: (editedDepot: UpdateDepotInput) =>
    depotApi.updateDepot({
      id: props.depotId,
      updateDepotInput: { ...editedDepot }
    }),
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
      updateDepot(formRef.value?.getFieldsValue() as UpdateDepotInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
