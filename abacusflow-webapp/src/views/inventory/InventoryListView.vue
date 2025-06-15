<template>
  <div class="inventory-list">
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
              <router-link :to="`/inventory/edit/${record.id}`">编辑</router-link>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除这个库存记录吗？" @confirm="handleDelete(record)">
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import { InventoryApi, ProductApi, WarehouseApi } from "@/core/openapi/apis";
import type { BasicInventory, BasicProduct, BasicWarehouse } from "@/core/openapi/models";

const router = useRouter();
const inventoryApi = new InventoryApi();
const productApi = new ProductApi();
const warehouseApi = new WarehouseApi();

// 数据列表
const inventories = ref<BasicInventory[]>([]);
const products = ref<BasicProduct[]>([]);
const warehouses = ref<BasicWarehouse[]>([]);
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
  router.push("/inventory/create");
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

onMounted(() => {
  getInventories();
  getProducts();
  getWarehouses();
});
</script>

<style scoped>
.inventory-list {
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
</style>
