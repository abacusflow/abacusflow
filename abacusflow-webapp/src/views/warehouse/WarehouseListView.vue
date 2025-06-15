<template>
  <div class="warehouse-list">
    <div class="header">
      <h1>仓库管理</h1>
      <a-button type="primary" @click="handleAdd">新增仓库</a-button>
    </div>

    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="仓库名称">
          <a-input v-model:value="searchForm.name" placeholder="请输入仓库名称" allow-clear />
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
        :dataSource="warehouses"
        :columns="columns"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <router-link :to="`/warehouse/edit/${record.id}`">编辑</router-link>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除这个仓库吗？" @confirm="handleDelete(record)">
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
import { WarehouseApi } from "@/core/openapi/apis";
import type { BasicWarehouse } from "@/core/openapi/models";

const router = useRouter();
const warehouseApi = new WarehouseApi();

// 数据列表
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
    title: "仓库名称",
    dataIndex: "name"
  },
  {
    title: "地址",
    dataIndex: "address",
    ellipsis: true
  },
  {
    title: "联系人",
    dataIndex: "contact"
  },
  {
    title: "联系电话",
    dataIndex: "phone"
  },
  {
    title: "描述",
    dataIndex: "description",
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
  name: ""
});

// 获取仓库列表
const getWarehouses = async () => {
  loading.value = true;
  try {
    const response = await warehouseApi.listWarehouses();
    warehouses.value = response;
  } catch (error) {
    message.error("获取仓库列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  getWarehouses();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: ""
  };
  getWarehouses();
};

// 新增仓库
const handleAdd = () => {
  router.push("/warehouse/create");
};

// 删除仓库
const handleDelete = async (record: BasicWarehouse) => {
  try {
    await warehouseApi.deleteWarehouse({ id: record.id });
    message.success("删除成功");
    getWarehouses();
  } catch (error) {
    message.error("删除失败");
  }
};

onMounted(() => {
  getWarehouses();
});
</script>

<style scoped>
.warehouse-list {
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
