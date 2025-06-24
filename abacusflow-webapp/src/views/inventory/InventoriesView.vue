<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>库存管理</h1>
      </a-flex>

      <a-flex justify="flex-start" align="start" style="height: 100%">
        <ProductCategoryTreeComponent @categorySelected="onCategorySelected" />
        <a-flex vertical style="flex: 1; padding-left: 16px">
          <a-card :bordered="false">
            <a-form layout="inline" :model="searchForm">
              <a-form-item label="产品名" name="productName">
                <a-input
                  v-model:value="searchForm.productName"
                  placeholder="产品名字"
                  allow-clear
                />
              </a-form-item>
              <a-form-item label="储存点" name="depotName">
                <a-input v-model:value="searchForm.depotName" placeholder="储存点名" allow-clear />
              </a-form-item>

              <a-form-item label="商品类型" name="productType">
                <a-select v-model:value="searchForm.productType" placeholder="请选择商品类型">
                  <a-select-option
                    v-for="value in Object.values(ProductType)"
                    :key="value"
                    :value="value"
                  >
                    {{ $translateProductType(value) }}
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

          <a-card :bordered="false">
            <a-table
              :columns="columns"
              :data-source="pageData?.content || []"
              :loading="isPending"
              row-key="id"
              size="small"
              :pagination="pagination"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'quantity'">
                  <a-tooltip
                    :title="stockHealthTip(record.quantity, record.safetyStock, record.maxStock)"
                  >
                    <a-tag
                      :color="
                        stockHealthColor(record.quantity, record.safetyStock, record.maxStock)
                      "
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
        </a-flex>
      </a-flex>
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
import { computed, h, inject, reactive, ref } from "vue";
import { useQuery } from "@tanstack/vue-query";
import {
  type BasicInventory,
  type BasicInventoryUnit,
  type InventoryApi,
  InventoryUnitType,
  type ListInventoriesPageRequest,
  ProductType
} from "@/core/openapi";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import InventoryAssignDepotView from "./InventoryAssignDepotView.vue";
import InventoryEditWarningLineView from "./InventoryEditWarningLineView.vue";
import { translateProductType } from "@/util/productUtils";
import { type TableColumnsType, Tag } from "ant-design-vue";
import ProductCategoryTreeComponent from "@/components/product/ProductCategoryTreeComponent.vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const inventoryApi = inject("inventoryApi") as InventoryApi;

const pageIndex = ref(1);
const pageSize = ref(10);
const showAssignDepot = ref(false);
const showEditWarningLine = ref(false);
const editingInventory = ref<BasicInventory | null>(null);
const editingInventoryUnit = ref<BasicInventoryUnit | null>(null);

const productCategoryId = computed(() => {
  const id = route.query.productCategoryId;
  return id !== undefined ? Number(id) : undefined;
});

// 搜索表单
const searchForm = reactive({
  productName: undefined,
  depotName: undefined,
  productType: undefined
});

// 搜索
const handleSearch = () => {
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.productName = undefined;
  searchForm.depotName = undefined;
  searchForm.productType = undefined;
  pageIndex.value = 1;
  refetch();
};

const pagination = computed(() => ({
  current: pageIndex.value,
  pageSize: pageSize.value,
  total: pageData.value?.totalElements,
  showTotal: (total: number) => `共 ${total} 条`,
  onChange: (page: number, size: number) => {
    pageIndex.value = page;
    pageSize.value = size;
    refetch();
  }
}));

const {
  data: pageData,
  isPending,
  refetch
} = useQuery({
  queryKey: [
    "inventories",
    productCategoryId,
    searchForm.productName,
    searchForm.depotName,
    searchForm.productType,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { productName, depotName, productType } = searchForm;

    const params: ListInventoriesPageRequest = {
      productCategoryId: productCategoryId.value,
      productName: productName || undefined,
      depotName: depotName || undefined,
      productType: productType || undefined,
      pageIndex: pageIndex.value,
      pageSize: pageSize.value
    };
    return inventoryApi.listInventoriesPage(params);
  }
});

function handleAdjustWarningLine(inventory: BasicInventory) {
  editingInventory.value = inventory;
  showEditWarningLine.value = true;
}

function handleAssignDepot(inventoryUnit: BasicInventoryUnit) {
  editingInventoryUnit.value = inventoryUnit;
  showAssignDepot.value = true;
}

function onCategorySelected(productCategoryId: string | number) {
  router.push({
    path: route.path,
    query: {
      ...route.query,
      productCategoryId
    }
  });
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
