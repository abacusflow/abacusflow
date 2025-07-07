<template>
  <a-spin tip="加载中..." :spinning="isLoading">
    <a-select
      v-model:value="selectedProductId"
      placeholder="请选择产品"
      style="width: 200px; margin-bottom: 16px"
      size="small"
      allowClear
    >
      <a-select-option v-for="p in productOptions" :key="p.id" :value="p.id">
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
import { ref, computed, inject } from "vue";
import VChart from "vue-echarts";
import type { EChartsOption } from "echarts";
import cubejsApi from "@/plugin/cubejsApi";
import { useQuery } from "@tanstack/vue-query";
import { type ProductApi } from "@/core/openapi";
import dayjs from "dayjs";

const productApi = inject("productApi") as ProductApi;

const selectedProductId = ref<number | null>(null);
// 加载状态
const isLoading = computed(() => isLoadingPurchase.value || isLoadingSale.value);

// 产品选项数据
const { data: productOptions } = useQuery({
  queryKey: ["selectableProducts"],
  queryFn: () => productApi.listSelectableProducts()
});

//  采购价格数据
const { data: purchaseData, isLoading: isLoadingPurchase } = useQuery({
  queryKey: ["purchasePriceTrend", selectedProductId],
  queryFn: () =>
    cubejsApi.load({
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
    }),
  enabled: computed(() => !!selectedProductId.value)
});

// 销售价格数据
const { data: saleData, isLoading: isLoadingSale } = useQuery({
  queryKey: ["salePriceTrend", selectedProductId],
  queryFn: () =>
    cubejsApi.load({
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
    }),
  enabled: computed(() => !!selectedProductId.value)
});

// 构建图表配置
const chartOption = computed((): EChartsOption | null => {
  if (!purchaseData.value || !saleData.value) return null;

  const purchaseTrend = purchaseData.value.rawData().map((row) => {
    const rawDate = String(row["purchase_order.order_date"]);
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD");
    return {
      date: formattedDate,
      price: Number(row["purchase_order_item.avg_price"])
    };
  });

  const saleTrend = saleData.value.rawData().map((row) => {
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
