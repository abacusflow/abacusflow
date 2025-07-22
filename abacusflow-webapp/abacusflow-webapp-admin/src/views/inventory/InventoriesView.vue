<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>库存管理</h1>
        <a-space>
          <a-dropdown>
            <a-button type="primary"> 导出库存 <DownOutlined /> </a-button>
            <template #overlay>
              <a-menu @click="({ key }: any) => handleExportInventories(key)">
                <a-menu-item key="excel">导出 Excel</a-menu-item>
                <a-menu-item key="pdf">导出 PDF</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
          <a-button type="primary" @click="handlePrintInventories"> 打印库存 </a-button>
        </a-space>
      </a-flex>

      <a-flex justify="flex-start" align="start" style="height: 100%">
        <a-flex vertical>
          <a-typography-text mark style="margin-bottom: 8px; text-align: center; display: block">
            打印/导出依据所选分类
          </a-typography-text>
          <ProductCategoryTreeComponent @categorySelected="onCategorySelected" />
        </a-flex>
        <a-flex vertical style="flex: 1; padding-left: 16px">
          <a-card :bordered="false">
            <a-form layout="inline" :model="searchForm">
              <a-form-item label="产品名" name="productName">
                <a-input
                  v-model:value="searchForm.productName"
                  placeholder="请输入产品名字"
                  allow-clear
                />
              </a-form-item>

              <a-form-item label="序列号/批次号" name="inventoryUnitCode">
                <a-input
                  v-model:value="searchForm.inventoryUnitCode"
                  placeholder="请输入序列号/批次号"
                  allow-clear
                />
              </a-form-item>

              <a-form-item label="产品类型" name="productType">
                <a-select v-model:value="searchForm.productType" placeholder="请选择产品类型">
                  <a-select-option
                    v-for="value in Object.values(ProductType)"
                    :key="value"
                    :value="value"
                  >
                    {{ $translateProductType(value) }}
                  </a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="储存点" name="depotName">
                <a-input
                  v-model:value="searchForm.depotName"
                  placeholder="请输入储存点名"
                  allow-clear
                />
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
            <a-button
              type="primary"
              ghost
              size="small"
              @click="showEmbeddedTable = !showEmbeddedTable"
            >
              {{ showEmbeddedTable ? "显示普通表格" : "显示内嵌表格" }}
            </a-button>
            <a-table
              v-if="showEmbeddedTable"
              :columns="inventoryColumns"
              :data-source="inventoriesPageData?.content || []"
              :loading="inventoriesIsPending"
              row-key="id"
              size="small"
              :pagination="pagination"
              v-model:expandedRowKeys="expandedRowKeys"
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
                <a-table
                  :columns="inventoryInnerColumns"
                  :data-source="record.units"
                  :pagination="false"
                >
                  <template #emptyText>
                    <p>暂无数据</p>
                  </template>

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

            <a-table
              v-if="!showEmbeddedTable"
              :columns="inventoryUnitColumns"
              :data-source="inventoryUnitsPageData?.content || []"
              :loading="inventoryUnitsIsPending"
              row-key="id"
              size="small"
              :pagination="pagination"
            ></a-table>
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
import ProductCategoryTreeComponent from "@/components/product/ProductCategoryTreeComponent.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import {
  type BasicInventory,
  type BasicInventoryUnit,
  ExportInventoryFormatEnum,
  type ExportInventoryRequest,
  type InventoryApi,
  InventoryUnitType,
  type ListBasicInventoriesPageRequest,
  type ListBasicInventoryUnitsPageRequest,
  ProductType
} from "@/core/openapi";
import { translateProductType } from "@/util/productUtils";
import { useMutation, useQuery } from "@tanstack/vue-query";
import { DownOutlined } from "@ant-design/icons-vue";
import { message, type TableColumnsType, Tag, Tooltip } from "ant-design-vue";
import { computed, h, inject, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import InventoryAssignDepotView from "./InventoryAssignDepotView.vue";
import InventoryEditWarningLineView from "./InventoryEditWarningLineView.vue";

const router = useRouter();
const route = useRoute();

const inventoryApi = inject("inventoryApi") as InventoryApi;

const pageIndex = ref(1);
const pageSize = ref(10);
const showAssignDepot = ref(false);
const showEditWarningLine = ref(false);
const editingInventory = ref<BasicInventory | null>(null);
const editingInventoryUnit = ref<BasicInventoryUnit | null>(null);
const expandedRowKeys = ref<number[]>([]);
const showEmbeddedTable = ref(true);

const productCategoryId = computed(() => {
  const id = route.query.productCategoryId;
  return id !== undefined ? Number(id) : undefined;
});

// 搜索表单
const searchForm = reactive({
  productName: undefined,
  depotName: undefined,
  inventoryUnitCode: undefined,
  productType: undefined
});

// 搜索
const handleSearch = () => {
  refetch();
};

// 刷新
const refetch = () => {
  if (showEmbeddedTable.value) {
    refetchInventories();
  } else {
    refetchInventoryUnits();
  }
};

// 重置搜索
const resetSearch = () => {
  searchForm.productName = undefined;
  searchForm.depotName = undefined;
  searchForm.inventoryUnitCode = undefined;
  searchForm.productType = undefined;
  pageIndex.value = 1;
  refetch();
};
const pagination = computed(() => {
  const pageData = showEmbeddedTable.value ? inventoriesPageData : inventoryUnitsPageData;

  return {
    current: pageIndex.value,
    pageSize: pageSize.value,
    total: pageData.value?.totalElements as number,
    showTotal: (total: number) => `共 ${total} 条`,
    onChange: (page: number, size: number) => {
      pageIndex.value = page;
      pageSize.value = size;
      refetch();
    }
  };
});

const {
  data: inventoriesPageData,
  isPending: inventoriesIsPending,
  refetch: refetchInventories
} = useQuery({
  queryKey: [
    "inventories",
    productCategoryId,
    searchForm.productName,
    searchForm.depotName,
    searchForm.productType,
    searchForm.inventoryUnitCode,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { productName, depotName, productType, inventoryUnitCode } = searchForm;

    const params: ListBasicInventoriesPageRequest = {
      productCategoryId: productCategoryId.value,
      productName: productName || undefined,
      inventoryUnitCode: inventoryUnitCode || undefined,
      depotName: depotName || undefined,
      productType: productType || undefined,
      pageIndex: pageIndex.value,
      pageSize: pageSize.value
    };
    return inventoryApi.listBasicInventoriesPage(params);
  }
});

const {
  data: inventoryUnitsPageData,
  isPending: inventoryUnitsIsPending,
  refetch: refetchInventoryUnits
} = useQuery({
  queryKey: [
    "inventoryUnits",
    productCategoryId,
    searchForm.productName,
    searchForm.depotName,
    searchForm.productType,
    searchForm.inventoryUnitCode,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { productName, depotName, productType, inventoryUnitCode } = searchForm;

    const params: ListBasicInventoryUnitsPageRequest = {
      productCategoryId: productCategoryId.value,
      productName: productName || undefined,
      inventoryUnitCode: inventoryUnitCode || undefined,
      depotName: depotName || undefined,
      productType: productType || undefined,
      pageIndex: pageIndex.value,
      pageSize: pageSize.value
    };
    return inventoryApi.listBasicInventoryUnitsPage(params);
  }
});

const { mutateAsync: fetchExportInventory } = useMutation({
  mutationFn: ({ format, productCategoryId }: ExportInventoryRequest) => {
    return inventoryApi.exportInventoryRaw({ format, productCategoryId });
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

function handleExportInventories(format: ExportInventoryFormatEnum) {
  const curProductCategoryId = productCategoryId;
  fetchExportInventory({
    format,
    productCategoryId: curProductCategoryId.value
  })
    .then((response) => {
      // 先获取 headers 信息
      const contentDisposition = response.raw.headers.get("Content-Disposition") || "";
      const match = contentDisposition.match(/filename="(.+?)"/);
      const filename = match?.[1] || `inventory-${new Date().toISOString().slice(0, 10)}.${format}`;

      // 再获取 blob，并将 filename 一起传递
      return response.value().then((blob) => ({ blob, filename }));
    })
    .then(({ blob, filename }) => {
      if (!(blob instanceof Blob)) {
        message.error("导出失败，请稍后重试");
        return;
      }
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");

      a.href = url;
      a.download = filename;
      a.style.display = "none";
      document.body.appendChild(a);
      a.click();

      // 清理
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);

      message.success(`导出成功：${filename}`);
    })
    .catch((error) => {
      console.error("导出失败", error);
      message.error("导出失败，请稍后重试");
    });
}

function handlePrintInventories() {
  const curProductCategoryId = productCategoryId;
  fetchExportInventory({
    format: "pdf",
    productCategoryId: curProductCategoryId.value
  })
    .then((response) => response.value())
    .then((blob) => {
      if (!(blob instanceof Blob)) {
        message.error("打印失败，请稍后重试");
        return;
      }

      const url = URL.createObjectURL(blob);
      const iframe = document.createElement("iframe");

      iframe.style.display = "none";
      iframe.src = url;
      iframe.onload = () => {
        iframe.contentWindow?.focus();
        iframe.contentWindow?.print();
      };

      document.body.appendChild(iframe);
    })
    .catch((error) => {
      console.error("打印库存报告失败：", error);
      message.error("打印失败，请稍后重试");
    });
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

// 内嵌表格的默认展开
watch(
  () => inventoriesPageData.value?.content,
  (newContent) => {
    // 只展开类型为 Asset 且有剩余库存的行
    expandedRowKeys.value =
      newContent
        ?.filter((item) => item.productType === ProductType.Asset) // 过滤出类型为 Asset 的行
        ?.filter((item) => item.units.reduce((sum, unit) => sum + unit.remainingQuantity, 0) > 0) // 过滤出剩余库存总和大于 0 的行
        ?.map((item) => item.id) || []; // 提取 id // 如果 newContent 为 null 或 undefined，则返回空数组
  },
  { immediate: true } // 在页面加载时立即执行一次
);

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

const inventoryColumns: StrictTableColumnsType<BasicInventory> = [
  { title: "产品名称", dataIndex: "productName", key: "productName" },
  { title: "产品规格", dataIndex: "productSpecification", key: "productSpecification" },
  {
    title: "产品类型",
    dataIndex: "productType",
    key: "productType",
    customRender: ({ text }) => translateProductType(text)
  },
  {
    title: "产品备注",
    dataIndex: "productNote",
    key: "productNote",
    width: 120,
    ellipsis: true,
    customRender: ({ text }) => {
      return h(Tooltip, { title: text, placement: "topLeft" }, () => text);
    }
  },
  {
    title: "可用总库存数量",
    dataIndex: "remainingQuantity",
    key: "remainingQuantity",
    customRender: ({ text }) => {
      return h(Tag, () => text.toString());
    }
  },
  { title: "总库存数量", dataIndex: "quantity", key: "quantity" },
  { title: "初始总库存数量", dataIndex: "initialQuantity", key: "initialQuantity" },
  {
    title: "存储点",
    dataIndex: "depotNames",
    key: "depotNames",
    customRender: ({ text }) => (Array.isArray(text) ? text.join(", ") : "-")
  },
  { title: "操作", key: "action" }
];

const inventoryInnerColumns: TableColumnsType<BasicInventoryUnit> = [
  {
    title: "序列号/批次号",
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
  {
    title: "可用库存数量",
    dataIndex: "remainingQuantity",
    key: "remainingQuantity",
    customRender: ({ text }) => {
      return h(Tag, () => text.toString());
    }
  },
  {
    title: "库存数量",
    dataIndex: "quantity",
    key: "quantity",
    customRender: ({ text, record }) => {
      const color = text === 0 ? "red" : text < record.remainingQuantity ? "orange" : "green";
      return h(Tag, { color }, () => text.toString());
    }
  },
  { title: "初始库存数量", dataIndex: "initialQuantity", key: "initialQuantity" },
  {
    title: "单价",
    dataIndex: "unitPrice",
    key: "unitPrice",
    customRender: ({ text }) => {
      return h(Tag, { color: "blue" }, () => text.toFixed(2));
    }
  },
  {
    title: "入库时间",
    dataIndex: "receivedAt",
    key: "receivedAt",
    customRender: ({ text }) => new Date(text).toLocaleString("zh-CN")
  },
  {
    title: "采购单号",
    dataIndex: "purchaseOrderNo",
    key: "purchaseOrderNo",
    width: 120,
    ellipsis: true,
    customRender: ({ text }) => {
      return h(Tooltip, { title: text, placement: "topLeft" }, () => text);
    }
  },
  {
    title: "销售单号",
    dataIndex: "saleOrderNos",
    key: "saleOrderNos",
    width: 120,
    ellipsis: true,
    customRender: ({ text }) => {
      const content = text?.join(", ") ?? "-";
      return h(Tooltip, { title: content, placement: "topLeft" }, () => content);
    }
  },
  { title: "储存点", dataIndex: "depotName", key: "depotName" },
  { title: "操作", key: "action" }
];

const inventoryUnitColumns: TableColumnsType<BasicInventoryUnit> = [
  {
    title: "库存单元名",
    dataIndex: "title",
    key: "title",
    width: 200
  },
  {
    title: "序列号/批次号",
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
  {
    title: "可用库存数量",
    dataIndex: "remainingQuantity",
    key: "remainingQuantity",
    customRender: ({ text }) => {
      return h(Tag, () => text.toString());
    }
  },
  {
    title: "库存数量",
    dataIndex: "quantity",
    key: "quantity",
    customRender: ({ text, record }) => {
      const color = text === 0 ? "red" : text < record.remainingQuantity ? "orange" : "green";
      return h(Tag, { color }, () => text.toString());
    }
  },
  { title: "初始库存数量", dataIndex: "initialQuantity", key: "initialQuantity" },
  {
    title: "单价",
    dataIndex: "unitPrice",
    key: "unitPrice",
    customRender: ({ text }) => {
      return h(Tag, { color: "blue" }, () => text.toFixed(2));
    }
  },
  {
    title: "入库时间",
    dataIndex: "receivedAt",
    key: "receivedAt",
    customRender: ({ text }) => new Date(text).toLocaleString("zh-CN")
  },
  {
    title: "采购单号",
    dataIndex: "purchaseOrderNo",
    key: "purchaseOrderNo",
    width: 120,
    ellipsis: true,
    customRender: ({ text }) => {
      return h(Tooltip, { title: text, placement: "topLeft" }, () => text);
    }
  },
  {
    title: "销售单号",
    dataIndex: "saleOrderNos",
    key: "saleOrderNos",
    width: 120,
    ellipsis: true,
    customRender: ({ text }) => {
      const content = text?.join(", ") ?? "-";
      return h(Tooltip, { title: content, placement: "topLeft" }, () => content);
    }
  },
  { title: "储存点", dataIndex: "depotName", key: "depotName" },
  { title: "操作", key: "action" }
];
</script>
