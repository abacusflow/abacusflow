<template>
  <a-spin tip="加载中..." :spinning="isLoading">
    <a-select
      v-model:value="selectedProductId"
      placeholder="请选择产品"
      style="min-width: 200px"
      size="small"
      allowClear
      show-search
      optionFilterProp="label"
    >
      <a-select-option v-for="p in productOptions" :key="p.id" :value="p.id" :label="p.name">
        {{ p.name }}
      </a-select-option>
    </a-select>

    <v-chart
      :option="chartOption || {}"
      autoresize
      style="height: 400px"
      v-if="!isLoading && chartOption"
    />

    <a-empty v-else description="暂无数据" />
  </a-spin>
</template>

<script setup lang="ts">
import { ref, computed, inject, watch, onMounted } from "vue";
import VChart from "vue-echarts";
import type { EChartsOption } from "echarts";
import cubejsApi from "@/plugin/cubejsApi";
import { useQuery } from "@tanstack/vue-query";
import { type ProductApi } from "@/core/openapi";
import dayjs from "dayjs";

const productApi = inject("productApi") as ProductApi;

const selectedProductId = ref<number | null>(null);

// 产品选项数据
const { data: productOptions } = useQuery({
  queryKey: ["selectableProducts"],
  queryFn: () => productApi.listSelectableProducts()
});


onMounted(() => {
  // 如果已经加载完成了数据，手动设置默认值
  if (productOptions.value && productOptions.value.length > 0 && selectedProductId.value === null) {
    selectedProductId.value = productOptions.value[0].id;
  }
});

const { data: blendedData, isLoading } = useQuery({
  queryKey: ["priceTrend", selectedProductId],
  queryFn: () =>
    cubejsApi.load([
      {
        measures: ["purchase_order_item.avg_price"],
        timeDimensions: [
          {
            dimension: "purchase_order.order_date",
            granularity: "day",
            dateRange: "Last 90 days"
          }
        ],
        filters: [
          {
            dimension: "product.id",
            operator: "equals",
            values: [String(selectedProductId.value)]
          }
        ]
      },
      {
        measures: ["sale_order_item.avg_price"],
        timeDimensions: [
          {
            dimension: "sale_order.order_date",
            granularity: "day",
            dateRange: "Last 90 days"
          }
        ],
        filters: [
          {
            dimension: "product.id",
            operator: "equals",
            values: [String(selectedProductId.value)]
          }
        ]
      }
    ]),
  enabled: computed(() => !!selectedProductId.value)
});

// 构建图表配置
const chartOption = computed((): EChartsOption | null => {
  if (!blendedData.value) return null;
  const results = blendedData.value.decompose();
  const purchaseData = results[0].rawData();
  const saleData = results[1].rawData();

  const purchaseTrend = purchaseData.map((row) => {
    const rawDate = String(row["purchase_order.order_date"]);
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD");
    return {
      date: formattedDate,
      price: Number(row["purchase_order_item.avg_price"])
    };
  });

  const saleTrend = saleData.map((row) => {
    const rawDate = String(row["sale_order.order_date"]);
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD");
    return {
      date: formattedDate,
      price: Number(row["sale_order_item.avg_price"])
    };
  });

  const allDates = [...new Set([...purchaseTrend, ...saleTrend].map((p) => p.date))].sort();

  const purchaseSeries = allDates.map(
    (date) => purchaseTrend.find((p) => p.date === date)?.price ?? null
  );
  const saleSeries = allDates.map((date) => saleTrend.find((p) => p.date === date)?.price ?? null);

  return {
    title: {
      text: "价格跟踪图",
      left: "center"
    },
    tooltip: {
      trigger: "axis"
    },
    legend: {
      top: 20,
      data: ["采购平均单价", "销售平均单价"]
    },
    xAxis: {
      type: "category",
      data: allDates
    },
    yAxis: {
      type: "value",
      name: "元"
    },
    series: [
      {
        name: "采购平均单价",
        type: "line",
        connectNulls: true,
        data: purchaseSeries
      },
      {
        name: "销售平均单价",
        type: "line",
        connectNulls: true,
        data: saleSeries
      }
    ]
  };
});
</script>
