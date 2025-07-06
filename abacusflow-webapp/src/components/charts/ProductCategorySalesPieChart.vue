<template>
  <v-chart :option="chartOption || {}" autoresize style="height: 400px" />
</template>
<script setup lang="ts">
import { computed } from "vue";
import VChart from "vue-echarts";
import { useQuery } from "@tanstack/vue-query";
import cubejsApi from "@/plugin/cubejsApi";
import type { EChartsOption } from "echarts";

const { data: chartData } = useQuery({
  queryKey: ["productCategorySalesPie"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["product.count"], // 每个分类下的产品数量（或你可以用 sale_order_item.quantity）
      dimensions: ["product_category.name"]
    })
});

const chartOption = computed((): EChartsOption | null => {
  if (!chartData.value) return null;

  const pieData = chartData.value.rawData().map((row) => ({
    name: String(row["product_category.name"] || "未分类"),
    value: Number(row["product.count"]) || 0
  }));

  return {
    title: {
      text: "商品分类占比",
      left: "center"
    },
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} 个产品 ({d}%)"
    },
    legend: {
      bottom: 10,
      type: "scroll"
    },
    series: [
      {
        type: "pie",
        radius: "60%",
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        },
        label: {
          formatter: "{b}: {d}%" // 分类名 + 占比
        }
      }
    ]
  };
});
</script>
