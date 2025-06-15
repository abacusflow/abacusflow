<template>
  <div class="inventory">
    <div class="header">
      <h1>库存管理</h1>
      <a-button type="primary" @click="handleAdd">新增库存</a-button>
    </div>

    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="产品名称">
          <a-input
            v-model:value="searchForm.productName"
            placeholder="请输入产品名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="仓库">
          <a-select
            v-model:value="searchForm.warehouseId"
            placeholder="请选择仓库"
            allow-clear
            style="width: 200px"
          >
            <a-select-option
              v-for="warehouse in warehouses"
              :key="warehouse.id"
              :value="warehouse.id"
            >
              {{ warehouse.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">搜索</a-button>
            <a-button @click="resetSearch">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="table-card">
      <a-table
        :loading="loading"
        :dataSource="inventories"
        :columns="columns"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除这个库存记录吗？" @confirm="handleDelete(record)">
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="modalType === 'add' ? '新增库存' : '编辑库存'"
      @ok="handleSubmit"
      @cancel="modalVisible = false"
    >
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
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="form.remark" :rows="3" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import type { FormInstance } from "ant-design-vue";
import { InventoryApi, ProductApi, WarehouseApi } from "@/core/openapi/apis";
import type {
  BasicInventory,
  Inventory,
  CreateInventoryInput,
  UpdateInventoryInput
} from "@/core/openapi/models";

const inventoryApi = new InventoryApi();
const productApi = new ProductApi();
const warehouseApi = new WarehouseApi();

// 数据列表
const inventories = ref<BasicInventory[]>([]);
const products = ref([]);
const warehouses = ref([]);
const loading = ref(false);

// 表格列定义
const columns = [
  {
    title: "ID",
    dataIndex: "id",
    width: 80
  },
  {
    title: "产品名称",
    dataIndex: ["product", "name"]
  },
  {
    title: "仓库名称",
    dataIndex: ["warehouse", "name"]
  },
  {
    title: "数量",
    dataIndex: "quantity"
  },
  {
    title: "备注",
    dataIndex: "remark",
    ellipsis: true
  },
  {
    title: "操作",
    key: "action",
    width: 200,
    fixed: "right"
  }
];

// 搜索表单
const searchForm = ref({
  productName: "",
  warehouseId: undefined
});

// 对话框相关
const modalVisible = ref(false);
const modalType = ref<"add" | "edit">("add");
const formRef = ref<FormInstance>();
const form = ref<CreateInventoryInput | UpdateInventoryInput>({
  productId: undefined,
  warehouseId: undefined,
  quantity: 0,
  remark: ""
});

// 表单验证规则
const rules = {
  productId: [{ required: true, message: "请选择产品" }],
  warehouseId: [{ required: true, message: "请选择仓库" }],
  quantity: [{ required: true, message: "请输入数量" }]
};

// 获取库存列表
const getInventories = async () => {
  loading.value = true;
  try {
    const response = await inventoryApi.listInventories();
    inventories.value = response;
  } catch (error) {
    message.error("获取库存列表失败");
  } finally {
    loading.value = false;
  }
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

// 搜索
const handleSearch = () => {
  getInventories();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    productName: "",
    warehouseId: undefined
  };
  getInventories();
};

// 新增库存
const handleAdd = () => {
  modalType.value = "add";
  form.value = {
    productId: undefined,
    warehouseId: undefined,
    quantity: 0,
    remark: ""
  };
  modalVisible.value = true;
};

// 编辑库存
const handleEdit = (record: BasicInventory) => {
  modalType.value = "edit";
  form.value = {
    productId: record.productId,
    warehouseId: record.warehouseId,
    quantity: record.quantity,
    remark: record.remark
  };
  modalVisible.value = true;
};

// 删除库存
const handleDelete = async (record: BasicInventory) => {
  try {
    await inventoryApi.deleteInventory({ id: record.id });
    message.success("删除成功");
    getInventories();
  } catch (error) {
    message.error("删除失败");
  }
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    if (modalType.value === "add") {
      await inventoryApi.addInventory({ createInventoryInput: form.value as CreateInventoryInput });
      message.success("新增成功");
    } else {
      const inventory = inventories.value.find(
        (i) => i.productId === form.value.productId && i.warehouseId === form.value.warehouseId
      );
      if (inventory) {
        await inventoryApi.updateInventory({
          id: inventory.id,
          updateInventoryInput: form.value as UpdateInventoryInput
        });
        message.success("编辑成功");
      }
    }
    modalVisible.value = false;
    getInventories();
  } catch (error) {
    message.error(modalType.value === "add" ? "新增失败" : "编辑失败");
  }
};

onMounted(() => {
  getInventories();
  getProducts();
  getWarehouses();
});
</script>

<style scoped>
.inventory {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-card {
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.danger-link {
  color: #ff4d4f;
}

.danger-link:hover {
  color: #ff7875;
}
</style>
