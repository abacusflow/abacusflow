<template>
  <a-form :model="formState" ref="formRef" @finish="handleOk">
    <a-form-item label="产品名" name="name" :rules="[{ required: true, message: '请输入产品名' }]">
      <a-input v-model:value="formState.name" />
    </a-form-item>
    <a-form-item
      label="分类"
      name="categoryId"
      :rules="[{ required: true, message: '请选择分类' }]"
    >
      <a-select v-model:value="formState.categoryId" placeholder="请选择分类">
        <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item
      label="供应商"
      name="supplierId"
      :rules="[{ required: true, message: '请输入供应商' }]"
    >
      <a-input v-model:value="formState.supplierId"></a-input>
    </a-form-item>

    <a-form-item label="单位" name="unit" :rules="[{ required: true, message: '请选择单位' }]">
      <a-select v-model:value="formState.unit" placeholder="请选择单位">
        <a-select-option v-for="value in Object.values(ProductUnit)" :key="value" :value="value">
          {{ $translateUnit(value) }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="单价" name="unitPrice" :rules="[{ required: true, message: '请输入单价' }]">
      <a-input-number
        v-model:value="formState.unitPrice"
        :min="0"
        :precision="2"
        style="width: 100%"
      />
    </a-form-item>

    <a-form-item label="规格说明" name="specification" extra="例如：'256GB','A4纸'">
      <a-textarea v-model:value="formState.specification" :rows="3" placeholder="请输入规格说明" />
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
import { ProductApi, ProductUnit, type CreateProductInput } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const formState = reactive<Partial<CreateProductInput>>({
  name: undefined,
  categoryId: undefined,
  unit: ProductUnit.Item,
  unitPrice: 0,
  specification: undefined,
  supplierId: undefined
});

const productApi = inject("productApi") as ProductApi;

const emit = defineEmits(["success", "update:visible"]);

const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listProductCategories()
});

const { mutate: createProduct } = useMutation({
  mutationFn: (newProduct: CreateProductInput) =>
    productApi.addProduct({ createProductInput: { ...newProduct } }),
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
      createProduct(formRef.value?.getFieldsValue() as CreateProductInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
