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

      <a-form-item
        label="供应商"
        name="supplierId"
        :rules="[{ required: true, message: '请输入供应商' }]"
      >
        <a-select v-model:value="formState.supplierId" placeholder="请选择供应商">
          <a-select-option v-for="supplier in suppliers" :key="supplier.id" :value="supplier.id">
            {{ supplier.name }}
          </a-select-option>
        </a-select>
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

      <a-form-item label="单位" name="unit" :rules="[{ required: true, message: '请选择单位' }]">
        <a-select v-model:value="formState.unit" placeholder="请选择单位">
          <a-select-option v-for="value in Object.values(ProductUnit)" :key="value" :value="value">
            {{ $translateUnit(value) }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item
        label="单价"
        name="unitPrice"
        :rules="[{ required: true, message: '请输入价格' }]"
      >
        <a-input-number
          v-model:value="formState.unitPrice"
          :min="0"
          :precision="2"
          style="width: 100%"
        />
      </a-form-item>

      <a-form-item label="规格说明" name="specification" extra="例如：'256GB','A4纸'">
        <a-textarea
          v-model:value="formState.specification"
          :rows="3"
          placeholder="请输入规格说明"
        />
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
import { PartnerApi, type ProductApi, ProductUnit, type UpdateProductInput } from "@/core/openapi";
import { useMutation, useQuery } from "@tanstack/vue-query";

const formRef = ref<FormInstance>();

const props = defineProps<{ productId: number }>();

const productApi = inject("productApi") as ProductApi;
const partnerApi = inject("partnerApi") as PartnerApi;

const emit = defineEmits(["success", "close", "update:visible"]);

const formState = reactive<Partial<UpdateProductInput>>({
  name: undefined,
  categoryId: undefined,
  unit: ProductUnit.Item,
  unitPrice: 0,
  specification: undefined,
  supplierId: undefined
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

const { data: suppliers } = useQuery({
  queryKey: ["suppliers"],
  queryFn: () => partnerApi.listSuppliers()
});

const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listProductCategories()
});

// 当查询成功且有数据时，优先使用 API 数据
watchEffect(() => {
  if (isSuccess.value && fetchedProduct.value) {
    const { name, categoryId, unit, unitPrice, specification, supplierId } = fetchedProduct.value;
    formState.name = name;
    formState.categoryId = categoryId;
    formState.unit = unit;
    formState.unitPrice = unitPrice;
    formState.specification = specification;
    formState.supplierId = supplierId;
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
