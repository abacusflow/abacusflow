<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>库存管理</h1>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="产品" name="productId">
            <a-input v-model:value="searchForm.productId" placeholder="请选择产品" allow-clear />
          </a-form-item>

          <a-form-item label="仓库" name="warehouseId">
            <a-input v-model:value="searchForm.warehouseId" placeholder="请选择仓库" allow-clear />
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">搜索</a-button>
              <a-button @click="resetSearch">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <a-card :bordered="false">
        <a-table :columns="columns" :data-source="data" :loading="isPending" :row-key="'id'">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" @click="assignWarehouse(record.id)">分配仓库</a-button>

                <a-divider type="vertical" />

                <a-popconfirm title="确定减库存？" @confirm="handleDecreaseInventory(record.id)">
                  <a-button type="link">减库存</a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <a-popconfirm title="确定加库存？" @confirm="handleIncreaseInventory(record.id)">
                  <a-button type="link">加库存</a-button>
                </a-popconfirm>

                <a-divider type="vertical" />

                <a-button type="link" @click="adjustWarningLine(record.id)">调整预警线</a-button>

                <a-divider type="vertical" />

                <a-button type="link" @click="reserve(record.id)">冻结库存</a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
  </div>
</template>

<script lang="ts" setup>
import { ref, inject } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { InventoryApi, BasicInventory } from "@/core/openapi";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";

const inventoryApi = inject("inventoryApi") as InventoryApi;
const queryClient = useQueryClient();

// 搜索表单
const searchForm = ref({
  productId: undefined,
  warehouseId: undefined
});

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    productId: undefined,
    warehouseId: undefined
  };
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["inventories"],
  queryFn: () => inventoryApi.listInventories()
});

const { mutate: increaseInventory } = useMutation({
  mutationFn: ({ id, amount }: InventoryUpdateParams) =>
    inventoryApi.increaseInventory({ id, increaseInventoryRequest: { amount } }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["inventories"] });
    message.success("库存成功加1");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

const { mutate: decreaseInventory } = useMutation({
  mutationFn: ({ id, amount }: InventoryUpdateParams) =>
    inventoryApi.decreaseInventory({ id, increaseInventoryRequest: { amount } }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["inventories"] });
    message.success("库存成功减1");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

function handleIncreaseInventory(id: number) {
  increaseInventory({ id, amount: 1 });
}

function handleDecreaseInventory(id: number) {
  decreaseInventory({ id, amount: 1 });
}

function assignWarehouse(id: number) {}

function adjustWarningLine(id: number) {}

function reserve(id: number) {
  message.warning("冻结库存功能尚未实现");
}

// 定义参数类型
type InventoryUpdateParams = {
  id: number;
  amount: number;
};

const columns: StrictTableColumnsType<BasicInventory> = [
  { title: "商品名称", dataIndex: "productName", key: "productName" },
  { title: "仓库名称", dataIndex: "warehouseName", key: "warehouseName" },
  { title: "总库存数量", dataIndex: "quantity", key: "quantity" },
  { title: "可用数量", dataIndex: "availableQuantity", key: "availableQuantity" },
  { title: "安全库存预警线", dataIndex: "safetyStock", key: "safetyStock" },
  { title: "最大库存预警线", dataIndex: "maxStock", key: "maxStock" },
  { title: "操作", key: "action" }
];
</script>
