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
        <a-table
          :columns="columns"
          :data-source="data"
          :loading="isPending"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'quantity'">
              <a-tooltip
                :title="stockHealthTip(record.quantity, record.safetyStock, record.maxStock)"
              >
                <a-tag
                  :color="stockHealthColor(record.quantity, record.safetyStock, record.maxStock)"
                >
                  {{ record.quantity }}
                </a-tag>
              </a-tooltip>
            </template>

            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleAdjustWarningLine(record)"
                  >调整预警线</a-button
                >
              </a-space>
            </template>
          </template>

          <template #expandedRowRender="{ record }">
            <a-table :columns="innerColumns" :data-source="record.units" :pagination="false">
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'action'">
                  <a-space>
                    <a-button type="link" shape="circle" @click="handleAssignDepot(record)"
                      >分配储存点</a-button
                    >
                  </a-space>
                </template>
              </template>
            </a-table>
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
        v-if="showAssignDepot && editingInventoryUnit"
        v-model:visible="showAssignDepot"
        :inventoryUnitId="editingInventoryUnit.id"
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
import { h, inject, ref } from "vue";
import { useQuery, useQueryClient } from "@tanstack/vue-query";
import {
  InventoryUnitType,
  type BasicInventory,
  type BasicInventoryUnit,
  type InventoryApi
} from "@/core/openapi";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import InventoryAssignDepotView from "./InventoryAssignDepotView.vue";
import InventoryEditWarningLineView from "./InventoryEditWarningLineView.vue";
import { translateProductType } from "@/util/productUtils";
import { Tag, type TableColumnsType } from "ant-design-vue";

const inventoryApi = inject("inventoryApi") as InventoryApi;
const queryClient = useQueryClient();

const showAssignDepot = ref(false);
const showEditWarningLine = ref(false);
const editingInventory = ref<BasicInventory | null>(null);
const editingInventoryUnit = ref<BasicInventoryUnit | null>(null);
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

function handleAdjustWarningLine(inventory: BasicInventory) {
  editingInventory.value = inventory;
  showEditWarningLine.value = true;
}

function handleAssignDepot(inventoryUnit: BasicInventoryUnit) {
  editingInventoryUnit.value = inventoryUnit;
  showAssignDepot.value = true;
}

const stockHealthTip = (value: number = 0, min: number = 0, max: number = Infinity): string => {
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

const stockHealthColor = (value: number = 0, min: number = 0, max: number = Infinity): string => {
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
  {
    title: "商品类型",
    dataIndex: "productType",
    key: "productType",
    customRender: ({ text }) => translateProductType(text)
  },
  { title: "总库存数量", dataIndex: "quantity", key: "quantity" },
  {
    title: "存储点",
    dataIndex: "depotNames",
    key: "depotNames",
    customRender: ({ text }) => (Array.isArray(text) ? text.join(", ") : "-")
  },
  { title: "安全库存预警线", dataIndex: "safetyStock", key: "safetyStock" },
  { title: "最大库存预警线", dataIndex: "maxStock", key: "maxStock" },
  { title: "操作", key: "action" }
];

const innerColumns: TableColumnsType<BasicInventoryUnit> = [
  // { title: "库存单元名", dataIndex: "title", key: "title" },
  {
    title: "批次号/序列号",
    key: "identity",
    customRender: ({ record }) => {
      switch (record.type) {
        case InventoryUnitType.batch:
          return record.batchCode ?? "-";
        case InventoryUnitType.instance:
          return record.serialNumber ?? "-";
        default:
          return "-";
      }
    }
  },
  { title: "储存点", dataIndex: "depotName", key: "depotName" },
  { title: "数量", dataIndex: "quantity", key: "quantity" },
  {
    title: "剩余数量",
    dataIndex: "remainingQuantity",
    key: "remainingQuantity",
    customRender: ({ text, record }) => {
      const color = text === 0 ? "red" : text < record.remainingQuantity ? "orange" : "green";
      return h(Tag, { color }, () => text.toString());
    }
  },
  {
    title: "单价",
    dataIndex: "unitPrice",
    key: "unitPrice",
    customRender: ({ text }) => text.toFixed(2)
  },
  {
    title: "入库时间",
    dataIndex: "receivedAt",
    key: "receivedAt",
    customRender: ({ text }) => new Date(text).toLocaleString("zh-CN")
  },
  { title: "采购单号", dataIndex: "purchaseOrderNo", key: "purchaseOrderNo" },
  {
    title: "销售单号",
    dataIndex: "saleOrderNos",
    key: "saleOrderNos",
    customRender: ({ text }) => text?.join(", ") ?? "-"
  },
  { title: "操作", key: "action" }
];
</script>
