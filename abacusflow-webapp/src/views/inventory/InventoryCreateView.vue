<template>
  <div class="inventory-create">
    <div class="header">
      <h1>新增库存</h1>
    </div>

    <a-card>
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="产品" name="productId">
          <a-select v-model:value="form.productId" placeholder="请选择产品">
            <a-select-option v-for="product in products" :key="product.id" :value="product.id">
              {{ product.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="仓库" name="warehouseId">
          <a-select v-model:value="form.warehouseId" placeholder="请选择仓库">
            <a-select-option
              v-for="warehouse in warehouses"
              :key="warehouse.id"
              :value="warehouse.id"
            >
              {{ warehouse.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数量" name="quantity">
          <a-input-number v-model:value="form.quantity" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="安全库存" name="safetyStock">
          <a-input-number v-model:value="form.safetyStock" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="最大库存" name="maxStock">
          <a-input-number v-model:value="form.maxStock" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 6 }">
          <a-space>
            <a-button type="primary" @click="handleSubmit">保存</a-button>
            <a-button @click="handleCancel">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import type { FormInstance } from "ant-design-vue";
import { InventoryApi, ProductApi, WarehouseApi } from "@/core/openapi/apis";
import type { BasicProduct, BasicWarehouse, CreateInventoryInput } from "@/core/openapi/models";

const router = useRouter();
const inventoryApi = new InventoryApi();
const productApi = new ProductApi();
const warehouseApi = new WarehouseApi();

const formRef = ref<FormInstance>();
const form = ref<CreateInventoryInput>({
  productId: 0,
  warehouseId: 0,
  quantity: 0,
  safetyStock: null,
  maxStock: null
});

const products = ref<BasicProduct[]>([]);
const warehouses = ref<BasicWarehouse[]>([]);

// 表单验证规则
const rules = {
  productId: [{ required: true, message: "请选择产品" }],
  warehouseId: [{ required: true, message: "请选择仓库" }],
  quantity: [{ required: true, message: "请输入数量" }]
};

// 获取产品列表
const getProducts = async () => {
  try {
    const response = await productApi.listProducts();
    products.value = response;
  } catch (error) {
    message.error("获取产品列表失败");
  }
};

// 获取仓库列表
const getWarehouses = async () => {
  try {
    const response = await warehouseApi.listWarehouses();
    warehouses.value = response;
  } catch (error) {
    message.error("获取仓库列表失败");
  }
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    await inventoryApi.addInventory({ createInventoryInput: form.value });
    message.success("新增成功");
    router.push("/inventory");
  } catch (error) {
    message.error("新增失败");
  }
};

// 取消
const handleCancel = () => {
  router.push("/inventory");
};

onMounted(() => {
  getProducts();
  getWarehouses();
});
</script>

<style scoped>
.inventory-create {
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}
</style>
