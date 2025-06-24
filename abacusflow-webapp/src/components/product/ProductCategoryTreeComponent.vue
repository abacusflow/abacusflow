<template>
  <div>
    <a-input-search v-model:value="searchValue" style="margin-bottom: 8px" placeholder="搜索分类" />

    <a-tree
      :tree-data="treeData"
      :expanded-keys="expandedKeys"
      :auto-expand-parent="autoExpandParent"
      :loading="isPending"
      @expand="onExpand"
      :selected-keys="selectedKeys"
      @select="onSelect"
    >
      <template #title="{ title }">
        <span v-if="title.includes(searchValue)">
          {{ title.slice(0, title.indexOf(searchValue)) }}
          <span style="color: #f50">{{ searchValue }}</span>
          {{ title.slice(title.indexOf(searchValue) + searchValue.length) }}
        </span>
        <span v-else>{{ title }}</span>
      </template>
    </a-tree>
  </div>
</template>

<script lang="ts" setup>
import {computed, inject, ref, watch} from "vue";
import {useQuery} from "@tanstack/vue-query";
import type {ProductApi} from "@/core/openapi";

const productApi = inject("productApi") as ProductApi;

// ✅ 扁平数据 -> 树结构
interface TreeCategory {
  key: string | number;
  title: string;
  children?: TreeCategory[];
}

// ✅ 搜索状态管理
const searchValue = ref("");
const expandedKeys = ref<(string | number)[]>([]);
const autoExpandParent = ref(true);

const emit = defineEmits<{
  (e: "categorySelected", categoryId: string | number): void;
}>();

const selectedKeys = ref<(string | number)[]>([]);

// ✅ 查询数据
const { data, isPending } = useQuery({
  queryKey: ["productCategories"],
  queryFn: () => productApi.listProductCategories()
});

const treeData = computed<TreeCategory[]>(() => {
  const flat = data.value || [];
  const map = new Map<string, TreeCategory[]>();

  for (const category of flat) {
    const parent = category.parentName ?? "__root__";
    if (!map.has(parent)) map.set(parent, []);
    map.get(parent)!.push({
      key: category.id,
      title: category.name
    });
  }

  const buildTree = (parentName: string): TreeCategory[] => {
    const children = map.get(parentName) || [];
    for (const child of children) {
      const subChildren = buildTree(child.title);
      if (subChildren.length > 0) {
        child.children = subChildren;
      }
    }
    return children;
  };

  return buildTree("__root__");
});

// 树节点点击
function onSelect(selectedKeysValue: (string | number)[]) {
  selectedKeys.value = selectedKeysValue;
  if (selectedKeysValue.length > 0) {
    emit("categorySelected", selectedKeysValue[0]);
  }
}

// ✅ 构建扁平 dataList 供搜索用
const dataList = computed(() => {
  const list: { key: string | number; title: string }[] = [];

  const traverse = (nodes: TreeCategory[]) => {
    for (const node of nodes) {
      list.push({ key: node.key, title: node.title });
      if (node.children) {
        traverse(node.children);
      }
    }
  };

  traverse(treeData.value);
  return list;
});

// ✅ 获取某节点的父 key
function getParentKey(key: string | number, tree: TreeCategory[]): string | number | undefined {
  let parentKey: string | number | undefined;
  for (const node of tree) {
    if (node.children?.some((child) => child.key === key)) {
      parentKey = node.key;
    } else if (node.children) {
      const deeper = getParentKey(key, node.children);
      if (deeper) parentKey = deeper;
    }
  }
  return parentKey;
}

// ✅ 搜索监听，更新展开节点
watch(searchValue, (value) => {
  const expanded = dataList.value
    .map((item) => (item.title.includes(value) ? getParentKey(item.key, treeData.value) : null))
    .filter((v, i, arr): v is string | number => !!v && arr.indexOf(v) === i);

  expandedKeys.value = expanded;
  autoExpandParent.value = true;
});

// ✅ 用户手动展开节点
function onExpand(keys: string[]) {
  expandedKeys.value = keys;
  autoExpandParent.value = false;
}

function getAllKeys(tree: TreeCategory[]): (string | number)[] {
  const keys: (string | number)[] = [];

  const dfs = (nodes: TreeCategory[]) => {
    for (const node of nodes) {
      keys.push(node.key);
      if (node.children?.length) {
        dfs(node.children);
      }
    }
  };

  dfs(tree);
  return keys;
}

watch(
  treeData,
  (tree) => {
    expandedKeys.value = getAllKeys(tree);
  },
  { immediate: true }
);
</script>
