<template>
  <a-spin :spinning="isPending">
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
  </a-spin>
</template>

<script lang="ts" setup>
import {inject, reactive, ref, watchEffect} from "vue";
import {type FormInstance, message} from "ant-design-vue";
import type {ProductApi, UpdateProductCategoryInput} from "@/core/openapi";
import {useMutation, useQuery} from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ productCategoryId: number }>();

const productApi = inject("productApi") as ProductApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateProductCategoryInput>>({
  name: undefined,
  description: undefined,
  parentId: undefined
});

// TODO: 当 props.productCategoryId 变化时，没有重新获取产品类别数据，现在是在外层销毁重建了
const {
  data: fetchedProductCategory,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["productCategory", props.productCategoryId],
  queryFn: () => productApi.getProductCategory({ id: props.productCategoryId })
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedProductCategory.value) {
    const { name, parentId, description } = fetchedProductCategory.value;
    formState.name = name;
    formState.parentId = parentId;
    formState.description = description;
  }
});

const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listProductCategories()
});

const { mutate: updateProductCategory } = useMutation({
  mutationFn: (editedProductCategory: UpdateProductCategoryInput) =>
    productApi.updateProductCategory({
      id: props.productCategoryId,
      updateProductCategoryInput: { ...editedProductCategory }
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
      updateProductCategory(formRef.value?.getFieldsValue() as UpdateProductCategoryInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
