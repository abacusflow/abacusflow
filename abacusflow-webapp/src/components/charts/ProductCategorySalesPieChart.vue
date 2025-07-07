<template>
  <v-chart :option="chartOption || {}" autoresize style="height: 400px" />
</template>
<script setup lang="ts">
import { computed } from "vue";
import VChart from "vue-echarts";
import { useQuery } from "@tanstack/vue-query";
import cubejsApi from "@/plugin/cubejsApi";
import type { EChartsOption } from "echarts";

// 分别查询商品数量和库存数量
const { data: productData } = useQuery({
  queryKey: ["productCategorySales"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["product.count"],
      dimensions: ["product_category.name"]
    })
});

const { data: inventoryData } = useQuery({
  queryKey: ["inventoryCategorySales"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["inventory_unit.count"],
      dimensions: ["product_category.name"]
    })
});

const chartOption = computed((): EChartsOption | null => {
  if (!productData.value || !inventoryData.value) return null;

  const productPieData = productData.value.rawData().map((row) => ({
    name: String(row["product_category.name"] || "未分类"),
    value: Number(row["product.count"]) || 0
  }));

  const inventoryPieData = inventoryData.value.rawData().map((row) => ({
    name: String(row["product_category.name"] || "未分类"),
    value: Number(row["inventory_unit.count"]) || 0
  }));

  return {
    title: [
      { text: "商品数量占比", left: "25%", top: 10, textAlign: "center" },
      { text: "库存数量占比", left: "75%", top: 10, textAlign: "center" }
    ],
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)"
    },
    legend: {
      bottom: 10,
      type: "scroll"
    },
    series: [
      {
        type: "pie",
        radius: "50%",
        center: ["25%", "50%"],
        data: productPieData,
        label: {
          formatter: "{b}: {d}%"
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        }
      },
      {
        type: "pie",
        radius: "50%",
        center: ["75%", "50%"],
        data: inventoryPieData,
        label: {
          formatter: "{b}: {d}%"
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        }
      }
    ]
  };
});
</script>
