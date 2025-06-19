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

          <a-form-item label="储存点" name="depotId">
            <a-input v-model:value="searchForm.depotId" placeholder="请选择储存点" allow-clear />
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
            <template v-if="column.key === 'availableQuantity'">
              <a-tooltip :title="stockHealthTip(record)">
                <a-tag :color="stockHealthColor(record)">
                  {{ record.availableQuantity }}
                </a-tag>
              </a-tooltip>
            </template>

            <template v-if="column.key === 'expectedQuantity'">
              <a-tooltip :title="expectedQuantityTooltip(record)">
                <a-tag :color="expectedQuantityTagColor(record)">
                  {{ record.expectedQuantity }}
                </a-tag>
              </a-tooltip>
            </template>

            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleAssignDepot(record)"
                  >分配储存点</a-button
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
      title="分配储存点"
      :open="showAssignDepot"
      :closable="false"
      @close="showAssignDepot = false"
    >
      <InventoryAssignDepotView
        v-if="showAssignDepot && editingInventory"
        v-model:visible="showAssignDepot"
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
import { inject, ref } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type {
  BasicInventory,
  IncreaseInventoryRequest,
  InventoryApi,
  ReserveInventoryRequest
} from "@/core/openapi";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import InventoryAssignDepotView from "./InventoryAssignDepotView.vue";
import InventoryEditWarningLineView from "./InventoryEditWarningLineView.vue";

const inventoryApi = inject("inventoryApi") as InventoryApi;
const queryClient = useQueryClient();

const showAssignDepot = ref(false);
const showEditWarningLine = ref(false);
const increaseValue = ref<number>(1);
const decreaseValue = ref<number>(1);
const reserveValue = ref<number>(1);
const editingInventory = ref<BasicInventory | null>(null);
// 搜索表单
const searchForm = ref({
  productId: undefined,
  depotId: undefined
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
    depotId: undefined
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

function handleIncreaseInventory(inventory: BasicInventory, amount: number) {
  increaseInventory({ id: inventory.id, amount });
}

function handleDecreaseInventory(inventory: BasicInventory, amount: number) {
  decreaseInventory({ id: inventory.id, amount });
}

function handleReserveInventory(inventory: BasicInventory, amount: number) {
  reserveInventory({ id: inventory.id, amount });
}

function handleAssignDepot(inventory: BasicInventory) {
  editingInventory.value = inventory;
  showAssignDepot.value = true;
}

function handleAdjustWarningLine(inventory: BasicInventory) {
  editingInventory.value = inventory;
  showEditWarningLine.value = true;
}

function expectedQuantityTagColor(record: BasicInventory): string {
  return record.expectedQuantity === record.quantity ? "green" : "red";
}

function expectedQuantityTooltip(record: BasicInventory): string {
  if (record.expectedQuantity === record.quantity) {
    return "库存正常，预期与实际一致";
  } else {
    return `库存异常：预期为 ${record.expectedQuantity}，实际为 ${record.quantity}`;
  }
}

const stockHealthTip = (record: BasicInventory): string => {
  const value = record.availableQuantity ?? 0;
  const min = record.safetyStock ?? 0;
  const max = record.maxStock ?? Infinity;
  const range = max - min;

  if (value < min * 0.5) return "严重低于安全库存";
  if (value < min) return "低于安全库存";
  if (value > max * 1.2) return "严重超出最大库存";
  if (value > max) return "超出最大库存";

  if (range > 0) {
    const lower = min + range * 0.25;
    const upper = max - range * 0.25;

    if (value <= lower) return "库存偏低";
    if (value >= upper) return "库存偏高";
    return "库存适中";
  }

  return "库存健康";
};

const stockHealthColor = (record: BasicInventory): string => {
  const value = record.availableQuantity ?? 0;
  const min = record.safetyStock ?? 0;
  const max = record.maxStock ?? Infinity;
  const range = max - min;

  if (value < min * 0.5 || value > max * 1.2) return "red";
  if (value < min || value > max) return "orange";

  if (range > 0) {
    const lower = min + range * 0.25;
    const upper = max - range * 0.25;

    if (value <= lower) return "blue";
    if (value >= upper) return "cyan";
    return "green";
  }

  return "green";
};

const columns: StrictTableColumnsType<BasicInventory> = [
  { title: "商品名称", dataIndex: "productName", key: "productName" },
  { title: "储存点名称", dataIndex: "depotName", key: "depotName" },
  { title: "总库存数量", dataIndex: "quantity", key: "quantity" },
  { title: "可用数量", dataIndex: "availableQuantity", key: "availableQuantity" },
  { title: "期望数量", dataIndex: "expectedQuantity", key: "expectedQuantity" },
  { title: "安全库存预警线", dataIndex: "safetyStock", key: "safetyStock" },
  { title: "最大库存预警线", dataIndex: "maxStock", key: "maxStock" },
  { title: "操作", key: "action" }
];
</script>
