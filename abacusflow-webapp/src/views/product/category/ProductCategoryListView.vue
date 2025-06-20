<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>产品类别管理</h1>
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
        <a-table
          :columns="columns"
          :data-source="categoryTree"
          :loading="isPending"
          row-key="key"
          v-model:expandedRowKeys="expandedRowKeys"
          size="small"
        >
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
                  primary
                  type="link"
                  shape="circle"
                  @click="handleAddProductCategory(record)"
                  >新增</a-button
                >

                <a-divider type="vertical" />

                <a-button
                  type="link"
                  shape="circle"
                  :disabled="record.name == '根节点'"
                  @click="handleEditProductCategory(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm
                  title="确定删除该产品类别？"
                  @confirm="handleDeleteProductCategory(record.id)"
                  :disabled="record.name == '根节点'"
                >
                  <a-button shape="circle" type="link" :disabled="record.id === 1">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增产品类别" :open="showAdd" :closable="false" @close="showAdd = false">
      <ProductCategoryAddView
        v-if="showAdd && editingProductCategory"
        v-model:visible="showAdd"
        :parentCategoryId="editingProductCategory.key"
        @success="refetch"
      />
    </a-drawer>

    <a-drawer title="修改产品类别" :open="showEdit" :closable="false" @close="showEdit = false">
      <ProductCategoryEditView
        v-if="showEdit && editingProductCategory"
        v-model:visible="showEdit"
        :productCategoryId="editingProductCategory.key"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { computed, inject, ref, watch } from "vue";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import { message } from "ant-design-vue";
import type { BasicProductCategory, ProductApi } from "@/core/openapi";
import ProductCategoryAddView from "./ProductCategoryAddView.vue";
import ProductCategoryEditView from "./ProductCategoryEditView.vue";

interface TreeCategory {
  key: number;
  name: string;
  children?: TreeCategory[];
}

const productApi = inject("productApi") as ProductApi;
const queryClient = useQueryClient();

const showAdd = ref(false);
const showEdit = ref(false);
const editingProductCategory = ref<TreeCategory | null>(null);
const expandedRowKeys = ref<(string | number)[]>([]);

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

const handleAddProductCategory = (productCategory: TreeCategory) => {
  editingProductCategory.value = productCategory;
  showAdd.value = true;
};

const handleEditProductCategory = (productCategory: TreeCategory) => {
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

// 转成树结构
const categoryTree = computed<TreeCategory[]>(() => {
  const flat: BasicProductCategory[] = data.value || [];

  const map = new Map<string, TreeCategory[]>();

  for (const category of flat) {
    const parent = category.parentName ?? "__root__";
    if (!map.has(parent)) map.set(parent, []);
    map.get(parent)!.push({
      key: category.id,
      name: category.name
    });
  }

  const buildTree = (parentName: string): TreeCategory[] => {
    const children = map.get(parentName) || [];
    for (const child of children) {
      const subChildren = buildTree(child.name);
      if (subChildren.length > 0) {
        child.children = subChildren; // ✅ 仅当有子项时设置 children
      }
    }
    return children;
  };

  return buildTree("__root__");
});
// 监听树变化并更新展开项
watch(categoryTree, (tree) => {
  expandedRowKeys.value = getExpandedRowKeysFromTree(tree);
});

function getExpandedRowKeysFromTree(tree: TreeCategory[]): (string | number)[] {
  const keys: (string | number)[] = [];

  for (const root of tree) {
    keys.push(root.key); // ✅ 加入根节点
    if (root.children?.length) {
      for (const child of root.children) {
        keys.push(child.key);
      }
    }
  }

  return keys;
}

const columns: StrictTableColumnsType<BasicProductCategory> = [
  { title: "产品类别名", dataIndex: "name", key: "name" },
  { title: "操作", key: "action" }
];
</script>
