<template>
  <div class="product-category">
    <div class="header">
      <h1>产品分类管理</h1>
      <a-button type="primary" @click="handleAdd">新增分类</a-button>
    </div>

    <a-card class="table-card">
      <a-table
        :loading="loading"
        :dataSource="categories"
        :columns="columns"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定要删除这个分类吗？" @confirm="handleDelete(record)">
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="modalType === 'add' ? '新增分类' : '编辑分类'"
      @ok="handleSubmit"
      @cancel="modalVisible = false"
    >
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="分类名称" name="name">
          <a-input v-model:value="form.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="form.description" :rows="3" placeholder="请输入描述" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import type { FormInstance } from "ant-design-vue";
import { ProductApi } from "@/core/openapi/apis";
import type {
  BasicProductCategory,
  ProductCategory,
  CreateProductCategoryInput,
  UpdateProductCategoryInput
} from "@/core/openapi/models";
import { useQuery } from "@tanstack/vue-query";

const productApi = new ProductApi();

// 数据列表
const loading = ref(false);

// 表格列定义
const columns = [
  {
    title: "ID",
    dataIndex: "id",
    width: 80
  },
  {
    title: "分类名称",
    dataIndex: "name"
  },
  {
    title: "描述",
    dataIndex: "description",
    ellipsis: true
  },
  {
    title: "操作",
    key: "action",
    width: 200,
    fixed: "right"
  }
];

// 对话框相关
const modalVisible = ref(false);
const modalType = ref<"add" | "edit">("add");
const formRef = ref<FormInstance>();
const form = ref<CreateProductCategoryInput | UpdateProductCategoryInput>({
  name: "",
  description: ""
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: "请输入分类名称" }]
};

// 使用 Vue Query 获取分类列表
const { data: categories } = useQuery({
  queryKey: ["categories"],
  queryFn: () => productApi.listProductCategories()
});

// 新增分类
const handleAdd = () => {
  modalType.value = "add";
  form.value = {
    name: "",
    description: ""
  };
  modalVisible.value = true;
};

// 编辑分类
const handleEdit = (record: BasicProductCategory) => {
  modalType.value = "edit";
  form.value = {
    name: record.name,
    description: record.description
  };
  modalVisible.value = true;
};

// 删除分类
const handleDelete = async (record: BasicProductCategory) => {
  try {
    await productApi.deleteProductCategory({ id: record.id });
    message.success("删除成功");
    getCategories();
  } catch (error) {
    message.error("删除失败");
  }
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    if (modalType.value === "add") {
      await productApi.addProductCategory({
        createProductCategoryInput: form.value as CreateProductCategoryInput
      });
      message.success("新增成功");
    } else {
      const category = categories.value.find((c) => c.name === form.value.name);
      if (category) {
        await productApi.updateProductCategory({
          id: category.id,
          updateProductCategoryInput: form.value as UpdateProductCategoryInput
        });
        message.success("编辑成功");
      }
    }
    modalVisible.value = false;
    getCategories();
  } catch (error) {
    message.error(modalType.value === "add" ? "新增失败" : "编辑失败");
  }
};

onMounted(() => {
  getCategories();
});
</script>

<style scoped>
.product-category {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.danger-link {
  color: #ff4d4f;
}

.danger-link:hover {
  color: #ff7875;
}
</style>
