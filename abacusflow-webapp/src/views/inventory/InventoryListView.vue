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
            <template v-if="column.key === 'safetyStock'">
              <a-tag :color="safetyColor(record)">
                <template #icon>
                  <component :is="safetyIcon(record)" />
                </template>
                安全库存: {{ record.safetyStock }}
              </a-tag>
            </template>
            <template v-if="column.key === 'maxStock'">
              <a-tag :color="maxColor(record)">
                <template #icon>
                  <component :is="maxIcon(record)" />
                </template>
                最大库存: {{ record.maxStock }}
              </a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleAssignWarehouse(record)"
                  >分配仓库</a-button
                >

                <a-divider type="vertical" />

                <a-popover title="减少库存">
                  <template #content>
                    <a-flex justify="space-evenly" align="center">
                      <a-input-number
                        id="inputNumber"
                        v-model:value="decreaseValue"
                        :min="1"
                        :max="100"
                      />
                      <a-button
                        type="primary"
                        size="small"
                        @click="handleDecreaseInventory(record, decreaseValue)"
                        >确定</a-button
                      >
                    </a-flex>
                  </template>
                  <a-button type="link" shape="circle">减库存</a-button>
                </a-popover>

                <a-divider type="vertical" />

                <a-popover title="增加库存">
                  <template #content>
                    <a-flex justify="space-evenly" align="center">
                      <a-input-number
                        id="inputNumber"
                        v-model:value="increaseValue"
                        :min="1"
                        :max="100"
                      />
                      <a-button
                        type="primary"
                        size="small"
                        @click="handleIncreaseInventory(record, increaseValue)"
                        >确定</a-button
                      >
                    </a-flex>
                  </template>
                  <a-button type="link" shape="circle">加库存</a-button>
                </a-popover>

                <a-divider type="vertical" />

                <a-popover title="冻结库存">
                  <template #content>
                    <a-flex justify="space-evenly" align="center">
                      <a-input-number
                        id="inputNumber"
                        v-model:value="reserveValue"
                        :min="1"
                        :max="100"
                      />
                      <a-button
                        type="primary"
                        size="small"
                        @click="handleReserveInventory(record, reserveValue)"
                        >确定</a-button
                      >
                    </a-flex>
                  </template>
                  <a-button type="link" shape="circle">冻结库存</a-button>
                </a-popover>

                <a-divider type="vertical" />

                <a-button type="link" shape="circle" @click="handleAdjustWarningLine(record)"
                  >调整预警线</a-button
                >
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>

    <a-drawer
      title="分配仓库"
      :open="showAssignWarehouse"
      :closable="false"
      @close="showAssignWarehouse = false"
    >
      <InventoryAssignWarehouseView
        v-if="showAssignWarehouse && editingInventory"
        v-model:visible="showAssignWarehouse"
        :inventoryId="editingInventory.id"
        @success="refetch"
      />
    </a-drawer>

    <a-drawer
      title="调整预警线"
      :open="showEditWarningLine"
      :closable="false"
      @close="showEditWarningLine = false"
    >
      <InventoryEditWarningLineView
        v-if="showEditWarningLine && editingInventory"
        v-model:visible="showEditWarningLine"
        :inventoryId="editingInventory.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { ref, inject } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import {
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  CloseCircleOutlined
} from "@ant-design/icons-vue";
import type {
  InventoryApi,
  BasicInventory,
  IncreaseInventoryRequest,
  ReserveInventoryRequest,
  Inventory
} from "@/core/openapi";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import InventoryAssignWarehouseView from "./InventoryAssignWarehouseView.vue";
import InventoryEditWarningLineView from "./InventoryEditWarningLineView.vue";

const inventoryApi = inject("inventoryApi") as InventoryApi;
const queryClient = useQueryClient();

const showAssignWarehouse = ref(false);
const showEditWarningLine = ref(false);
const increaseValue = ref<number>(1);
const decreaseValue = ref<number>(1);
const reserveValue = ref<number>(1);
const editingInventory = ref<Inventory | null>(null);
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
  mutationFn: ({ id, amount }: { id: number } & IncreaseInventoryRequest) =>
    inventoryApi.increaseInventory({ id, increaseInventoryRequest: { amount } }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["inventories"] });
    message.success("加库存成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

const { mutate: decreaseInventory } = useMutation({
  mutationFn: ({ id, amount }: { id: number } & IncreaseInventoryRequest) =>
    inventoryApi.decreaseInventory({ id, increaseInventoryRequest: { amount } }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["inventories"] });
    message.success("减库存成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

const { mutate: reserveInventory } = useMutation({
  mutationFn: ({ id, amount }: { id: number } & ReserveInventoryRequest) =>
    inventoryApi.reserveInventory({ id, reserveInventoryRequest: { amount } }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["inventories"] });
    message.success("冻结库存成功");
  },
  onError: (error) => {
    message.error("操作失败");
    console.error(error);
  }
});

function handleIncreaseInventory(inventory: Inventory, amount: number) {
  increaseInventory({ id: inventory.id, amount });
}

function handleDecreaseInventory(inventory: Inventory, amount: number) {
  decreaseInventory({ id: inventory.id, amount });
}

function handleReserveInventory(inventory: Inventory, amount: number) {
  reserveInventory({ id: inventory.id, amount });
}

function handleAssignWarehouse(inventory: Inventory) {
  editingInventory.value = inventory;
  showAssignWarehouse.value = true;
}

function handleAdjustWarningLine(inventory: Inventory) {
  editingInventory.value = inventory;
  showEditWarningLine.value = true;
}
const safetyColor = (record: Inventory) => {
  const cur = record.availableQuantity;
  const safe = record.safetyStock;

  if (!safe) return "green";
  if (cur < safe * 0.5) return "red";
  if (cur < safe) return "orange";
  return "green";
};

const safetyIcon = (record: Inventory) => {
  const cur = record.availableQuantity;
  const safe = record.safetyStock;

  if (!safe) return undefined;
  if (cur < safe * 0.5) return CloseCircleOutlined;
  if (cur < safe) return ExclamationCircleOutlined;
  return CheckCircleOutlined;
};

const maxColor = (record: Inventory) => {
  const cur = record.availableQuantity;
  const max = record.maxStock;

  if (!max) return "green";
  if (cur > max * 1.2) return "red"; // 超出太多
  if (cur > max) return "orange"; // 稍微超出
  return "green";
};

const maxIcon = (record: Inventory) => {
  const cur = record.availableQuantity;
  const max = record.maxStock;

  if (!max) return undefined;
  if (cur > max * 1.2) return CloseCircleOutlined;
  if (cur > max) return ExclamationCircleOutlined;
  return CheckCircleOutlined;
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
