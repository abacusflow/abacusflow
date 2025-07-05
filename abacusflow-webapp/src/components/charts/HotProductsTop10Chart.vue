<template>
  <v-chart :option="chartOption || {}" autoresize style="height: 400px" />
</template>

<script setup lang="ts">
import { computed } from "vue";
import VChart from "vue-echarts";
import cubejsApi from "@/plugin/cubejsApi";
import type { EChartsOption } from "echarts";
import { useQuery } from "@tanstack/vue-query";

const { data: chatData } = useQuery({
  queryKey: ["hotProductsTop10Data"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["sale_order_item.quantity"],
      dimensions: ["product.name"],
      order: { "sale_order_item.quantity": "desc" },
      limit: 10
    })
});

const chartOption = computed((): EChartsOption | null => {
  if (!chatData.value) return null;
  const raw = chatData.value.rawData();
  return {
    title: { text: "热销 Top 10", left: "center" },
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    grid: {
      containLabel: true
    },
    xAxis: {
      type: "category",
      axisLabel: {
        rotate: 15
      },
      data: raw.map((r) => r["product.name"] as string)
    },
    yAxis: {
      type: "value",
      name: "销售数量",
      axisLabel: {
        formatter: "{value}"
      }
    },
    series: [
      {
        name: "销售数量",
        type: "bar",
        data: raw.map((r) => r["sale_order_item.quantity"] as number),
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: "#73c0de" },
              { offset: 1, color: "#1e90ff" }
            ]
          }
        }
      }
    ]
  };
});
</script>
