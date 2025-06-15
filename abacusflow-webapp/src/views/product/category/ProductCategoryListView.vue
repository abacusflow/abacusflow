<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>产品类别管理</h1>
        <a-button type="primary" @click="handleAddProductCategory" style="margin-bottom: 16px">
          新增产品类别
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="类别名">
            <a-input v-model:value="searchForm.keyword" placeholder="请输入类别名" allow-clear />
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
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'locked'">
              <a-switch v-model:checked="record.locked" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button
                  type="link"
                  :disabled="record.name === 'admin'"
                  @click="handleEditProductCategory(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定删除该产品类别？"
                  @confirm="handleDeleteProductCategory(record.id)"
                  :disabled="record.name === 'admin'"
                >
                  <a-button type="link" :disabled="record.name === 'admin'">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增产品类别" :open="showAdd" :closable="false" @close="showAdd = false">
      <ProductCategoryAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改产品类别" :open="showEdit" :closable="false" @close="showEdit = false">
      <ProductCategoryEditView
        v-if="showEdit && editingProductCategory"
        v-model:visible="showEdit"
        :productCategoryId="editingProductCategory.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { ref, inject } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import type { BasicProductCategory, ProductApi, ProductCategory } from "@/core/openapi";
import ProductCategoryAddView from "./ProductCategoryAddView.vue";
import ProductCategoryEditView from "./ProductCategoryEditView.vue";

const productApi = inject("productApi") as ProductApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingProductCategory = ref<ProductCategory | null>(null);
// 搜索表单
const searchForm = ref({
  keyword: "",
  categoryId: undefined
});

// 搜索
const handleSearch = () => {
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: "",
    categoryId: undefined
  };
  queryClient.invalidateQueries({ queryKey: ["products"] });
  refetch();
};

const handleAddProductCategory = () => (showAdd.value = true);
const handleEditProductCategory = (productCategory: ProductCategory) => {
  editingProductCategory.value = productCategory;
  showEdit.value = true;
};

const { data, isPending, refetch } = useQuery({
  queryKey: ["productCategories"],
  queryFn: () => productApi.listProductCategories()
});

const { mutate: deleteProductCategory } = useMutation({
  mutationFn: (id: number) => productApi.deleteProductCategory({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["productCategories"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteProductCategory(id: number) {
  deleteProductCategory(id);
}

const columns: StrictTableColumnsType<BasicProductCategory> = [
  { title: "产品类别名", dataIndex: "name", key: "name" },
  { title: "父类别名", dataIndex: "parentName", key: "parentName" },
  { title: "操作", key: "action" }
];
</script>
