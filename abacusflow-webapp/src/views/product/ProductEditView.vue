<template>
  <a-spin :spinning="isPending">
    <a-form :model="formState" ref="formRef" @finish="handleOk">
      <a-form-item
        label="产品名"
        name="name"
        :rules="[{ required: true, message: '请输入产品名' }]"
      >
        <a-input v-model:value="formState.name" />
      </a-form-item>

      <a-form-item label="产品规格" name="specification">
        <a-input v-model:value="formState.specification" />
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
        label="条形码"
        name="barcode"
        :rules="[{ required: true, message: '请输入产品条形码' }]"
      >
        <a-input v-model:value="formState.barcode" />
      </a-form-item>

      <a-form-item label="单位" name="unit" :rules="[{ required: true, message: '请选择单位' }]">
        <a-select v-model:value="formState.unit" placeholder="请选择单位">
          <a-select-option v-for="value in Object.values(ProductUnit)" :key="value" :value="value">
            {{ $translateProductUnit(value) }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="备注" name="note">
        <a-textarea v-model:value="formState.note" :rows="3" placeholder="请输入备注" />
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
import { type ProductApi, ProductUnit, type UpdateProductInput } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ productId: number }>();

const productApi = inject("productApi") as ProductApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateProductInput>>({
  name: undefined,
  specification: undefined,
  barcode: undefined,
  categoryId: undefined,
  unit: ProductUnit.Item,
  note: undefined
});

// TODO: 当 props.productId 变化时，没有重新获取产品数据，现在是在外层销毁重建了
const {
  data: fetchedProduct,
  isPending,
  isSuccess
} = useQuery({
  queryKey: ["product", props.productId],
  queryFn: () => productApi.getProduct({ id: props.productId })
});

const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listSelectableProductCategories()
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedProduct.value) {
    const { name, specification, barcode, categoryId, unit, note } = fetchedProduct.value;
    formState.name = name;
    formState.specification = specification;
    formState.barcode = barcode;
    formState.categoryId = categoryId;
    formState.unit = unit;
    formState.note = note;
  }
});

const { mutate: updateProduct } = useMutation({
  mutationFn: (editedProduct: UpdateProductInput) =>
    productApi.updateProduct({ id: props.productId, updateProductInput: { ...editedProduct } }),
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
      updateProduct(formRef.value?.getFieldsValue() as UpdateProductInput);
    })
    .catch((error) => {
      console.error("表单验证失败", error);
    });
};
</script>
