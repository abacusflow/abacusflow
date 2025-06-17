<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item
      label="产品类别名"
      name="name"
      :rules="[{ required: true, message: '请输入产品类别名' }]"
    >
      <a-input v-model:value="formState.name" />
    </a-form-item>

    <a-form-item
      label="父类别"
      name="parentId"
      :rules="[{ required: true, message: '请选择父类别' }]"
    >
      <a-select v-model:value="formState.parentId" placeholder="请选择父类别">
        <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="类别描述" name="description">
      <a-input v-model:value="formState.description" />
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
import type { CreateProductCategoryInput, ProductApi } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<Partial<CreateProductCategoryInput>>({
  name: undefined,
  parentId: undefined,
  description: undefined
});

const productApi = inject("productApi") as ProductApi;

const emit = defineEmits(["success", "update:visible"]);

const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listProductCategories()
});

const { mutate: createProductCategory } = useMutation({
  mutationFn: (newProductCategory: CreateProductCategoryInput) =>
    productApi.addProductCategory({ createProductCategoryInput: { ...newProductCategory } }),
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
      createProductCategory(formRef.value?.getFieldsValue() as CreateProductCategoryInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
