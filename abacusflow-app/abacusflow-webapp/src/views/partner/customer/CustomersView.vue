<template>
  <div>
    <a-space direction="vertical" style="width: 100%">
      <a-flex justify="space-between" align="center">
        <h1>客户管理</h1>
        <a-button type="primary" @click="handleAddCustomer" style="margin-bottom: 16px">
          新增客户
        </a-button>
      </a-flex>

      <a-card :bordered="false">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="客户名">
            <a-input v-model:value="searchForm.name" placeholder="客户名" allow-clear />
          </a-form-item>
          <a-form-item label="电话">
            <a-input v-model:value="searchForm.phone" placeholder="电话" allow-clear />
          </a-form-item>
          <a-form-item label="地址">
            <a-input v-model:value="searchForm.address" placeholder="地址" allow-clear />
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
            <template v-if="column.key === 'enabled'">
              <a-switch v-model:checked="record.enabled" disabled />
            </template>
            <template v-if="column.key === 'locked'">
              <a-switch v-model:checked="record.locked" disabled />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-button type="link" shape="circle" @click="handleEditCustomer(record)"
                  >编辑</a-button
                >

                <a-divider type="vertical" />

                <a-popconfirm title="确定删除该客户？" @confirm="handleDeleteCustomer(record.id)">
                  <a-button type="link" shape="circle">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </a-space>
    <a-drawer title="新增客户" :open="showAdd" :closable="false" @close="showAdd = false">
      <CustomerAddView v-if="showAdd" v-model:visible="showAdd" @success="refetch" />
    </a-drawer>

    <a-drawer title="修改客户" :open="showEdit" :closable="false" @close="showEdit = false">
      <CustomerEditView
        v-if="showEdit && editingCustomer"
        v-model:visible="showEdit"
        :customerId="editingCustomer.id"
        @success="refetch"
      />
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import type { StrictTableColumnsType } from "@/core/antdv/antdev-table";
import {
  type BasicCustomer,
  type Customer,
  type ListBasicCustomersPageRequest,
  PartnerApi
} from "@/core/openapi";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { message, Tag, Tooltip } from "ant-design-vue";
import { computed, h, inject, reactive, ref } from "vue";
import CustomerAddView from "./CustomerAddView.vue";
import CustomerEditView from "./CustomerEditView.vue";
import { dateToFormattedString } from "@/util/timestampUtils";

const partnerApi = inject("partnerApi") as PartnerApi;
const queryClient = useQueryClient();

const pageIndex = ref(1);
const pageSize = ref(10);
const showAdd = ref(false);
const showEdit = ref(false);
const editingCustomer = ref<Customer | null>(null);
// 搜索表单
const searchForm = reactive({
  name: undefined,
  phone: undefined,
  address: undefined
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
  refetch();
};

// 重置搜索
const resetSearch = () => {
  searchForm.name = undefined;
  searchForm.phone = undefined;
  searchForm.address = undefined;

  refetch();
};

const handleAddCustomer = () => (showAdd.value = true);
const handleEditCustomer = (customer: Customer) => {
  editingCustomer.value = customer;
  showEdit.value = true;
};

const {
  data: pageData,
  isPending,
  refetch
} = useQuery({
  queryKey: [
    "customers",
    searchForm.name,
    searchForm.phone,
    searchForm.address,
    pageIndex,
    pageSize
  ],
  queryFn: () => {
    const { name, phone, address } = searchForm;
    const params: ListBasicCustomersPageRequest = {
      pageIndex: pageIndex.value,
      pageSize: pageSize.value,
      name: name || undefined,
      phone: phone || undefined,
      address: address || undefined
    };
    return partnerApi.listBasicCustomersPage(params);
  }
});

const { mutate: deleteCustomer } = useMutation({
  mutationFn: (id: number) => partnerApi.deleteCustomer({ id }),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["customers"] });
    message.success("删除成功");
  },
  onError: (error) => {
    message.error("删除失败");
    console.error(error);
  }
});

function handleDeleteCustomer(id: number) {
  deleteCustomer(id);
}

const columns: StrictTableColumnsType<BasicCustomer> = [
  { title: "客户名", dataIndex: "name", key: "name" },
  { title: "联系电话", dataIndex: "phone", key: "phone" },
  {
    title: "联系地址",
    dataIndex: "address",
    key: "address",
    width: 260,
    ellipsis: true,
    customRender: ({ text }) => {
      return h(Tooltip, { title: text, placement: "topLeft" }, () => text);
    }
  },
  {
    title: "历史订单总数",
    dataIndex: "totalOrderCount",
    key: "totalOrderCount",
    customRender: ({ text }) => {
      return h(Tag, { color: "blue" }, () => text);
    }
  },
  {
    title: "历史订单总金额",
    dataIndex: "totalOrderAmount",
    key: "totalOrderAmount",
    customRender: ({ text }) => {
      return h(Tag, { color: "blue" }, () => text.toFixed(2));
    }
  },
  {
    title: "最近一次交易日期",
    dataIndex: "lastOrderDate",
    key: "lastOrderDate",
    customRender: ({ record }) => dateToFormattedString(record.lastOrderDate, "YYYY-MM-DD")
  },
  { title: "操作", key: "action" }
];
</script>
