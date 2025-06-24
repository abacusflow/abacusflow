<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>产品管理</h1>
        <a-button type="primary" @click="handleAddProduct" style="margin-bottom: 16px">
          新增产品
        </a-button>
      </a-flex>

      <a-flex justify="flex-start" align="start" style="height: 100%">
        <ProductCategoryTreeComponent @categorySelected="onCategorySelected" />
        <a-flex vertical style="flex: 1; padding-left: 16px">
          <a-card :bordered="false">
            <a-form layout="inline" :model="searchForm">
              <a-form-item label="产品名称">
                <a-input v-model:value="searchForm.name" placeholder="请输入产品名称" allow-clear />
              </a-form-item>

              <a-form-item label="类型" name="type">
                <a-select v-model:value="searchForm.type" placeholder="请选择商品类型">
                  <a-select-option
                    v-for="value in Object.values(ProductType)"
                    :key="value"
                    :value="value"
                  >
                    {{ $translateProductType(value) }}
                  </a-select-option>
                </a-select>
              </a-form-item>

              <a-form-item label="启用状态">
                <a-select
                  v-model:value="searchForm.enabled"
                  placeholder="请选择启用状态"
                  allow-clear
                  style="width: 120px"
                >
                  <a-select-option :value="true">启用</a-select-option>
                  <a-select-option :value="false">禁用</a-select-option>
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
                <template v-if="column.key === 'unit'">
                  {{ $translateProductUnit(record.unit) }}
                </template>
                <template v-if="column.key === 'type'">
                  {{ $translateProductType(record.type) }}
                </template>
                <template v-if="column.key === 'enabled'">
                  <a-switch v-model:checked="record.enabled" disabled />
                </template>
                <template v-if="column.key === 'note'">
                  <a-tooltip :title="record.note">
                    {{ record?.note?.length > 15 ? record?.note.slice(0, 15) + "…" : record?.note }}
                  </a-tooltip>
                </template>
                <template v-if="column.key === 'action'">
                  <a-space>
                    <a-button type="link" shape="circle" @click="handleEditProduct(record)"
                      >编辑
                    </a-button>

                    <a-divider type="vertical" />

                    <a-popconfirm
                      title="确定删除该产品？"
                      shape="circle"
                      @confirm="handleDeleteProduct(record.id)"
                    >
                      <a-button type="link">删除</a-button>
                    </a-popconfirm>
                  </a-space>
                </template>
              </template>

              <template #expandedRowRender="{ record }">
                <a-table
                  :columns="innerColumns"
                  :data-source="record.instances"
                  :pagination="false"
                >
                </a-table>
              </template>
            </a-table>
          </a-card>
        </a-flex>
      </a-flex>
    </a-space>
    <a-drawer title="新增产品" :open="showAdd" :closable="false" @close="showAdd = false">
      <ProductAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改产品" :open="showEdit" :closable="false" @close="showEdit = false">
      <ProductEditView
        v-if="showEdit && editingProduct"
        v-model:visible="showEdit"
        :productId="editingProduct.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { computed, inject, reactive, ref } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import ProductAddView from "./ProductAddView.vue";
import ProductEditView from "./ProductEditView.vue";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import ProductCategoryTreeComponent from "@/components/product/ProductCategoryTreeComponent.vue";
import { useRoute, useRouter } from "vue-router";
import {
  type ProductApi,
  type Product,
  type ListProductsPageRequest,
  type BasicProduct,
  type BasicProductInstancesInner,
  ProductType
} from "@/core/openapi";

const productApi = inject("productApi") as ProductApi;
const queryClient = useQueryClient();
const router = useRouter();
const route = useRoute();

const pageIndex = ref(1);
const pageSize = ref(10);
const showAdd = ref(false);
const showEdit = ref(false);
const editingProduct = ref<Product | null>(null);
const categoryId = computed(() => {
  const id = route.query.categoryId;
  return id !== undefined ? Number(id) : undefined;
});

// 搜索表单
const searchForm = reactive({
  name: undefined,
  type: undefined,
  enabled: undefined
});

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

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.name = undefined;
  searchForm.type = undefined;
  searchForm.enabled = undefined;

  refetch();
};

const handleAddProduct = () => (showAdd.value = true);
const handleEditProduct = (product: Product) => {
  editingProduct.value = product;
  showEdit.value = true;
};

const {
  data: pageData,
  isPending,
  refetch
} = useQuery({
  queryKey: [
    "products",
    categoryId,
    searchForm.name,
    searchForm.type,
    searchForm.enabled,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { name, type, enabled } = searchForm;
    const params: ListProductsPageRequest = {
      pageIndex: pageIndex.value,
      pageSize: pageSize.value,
      name: name || undefined,
      type: type || undefined,
      enabled: enabled || undefined,
      categoryId: categoryId.value || undefined
    };

    return productApi.listProductsPage(params);
  }
});

const { mutate: deleteProduct } = useMutation({
  mutationFn: (id: number) => productApi.deleteProduct({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["products"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function onCategorySelected(categoryId: string | number) {
  router.push({
    path: route.path,
    query: {
      ...route.query,
      categoryId
    }
  });
}
function handleDeleteProduct(id: number) {
  deleteProduct(id);
}

const columns: StrictTableColumnsType<BasicProduct> = [
  { title: "产品名称", dataIndex: "name", key: "name" },
  { title: "产品规格", dataIndex: "specification", key: "specification" },
  { title: "产品类型", dataIndex: "type", key: "type" },
  { title: "产品类别", dataIndex: "categoryName", key: "categoryName" },
  { title: "单位", dataIndex: "unit", key: "unit" },
  { title: "启用状态", dataIndex: "enabled", key: "enabled" },
  { title: "备注", dataIndex: "note", key: "note" },
  { title: "操作", key: "action" }
];

const innerColumns: StrictTableColumnsType<BasicProductInstancesInner> = [
  { title: "资产名称", dataIndex: "name", key: "name" },
  { title: "序列号", dataIndex: "serialNumber", key: "serialNumber" },
  { title: "单价", dataIndex: "unitPrice", key: "unitPrice" },
  { title: "操作", key: "action" }
];
</script>
