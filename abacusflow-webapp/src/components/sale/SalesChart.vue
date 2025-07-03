<template>
  <!-- <div style="width: 100%; height: 400px">
    <v-chart :option="chartOption || {}" autoresize />
  </div> -->

  <a-card>
    <div style="height: 400px">
      <v-chart :option="chartOption || {}" autoresize />
    </div>
  </a-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { use } from "echarts/core";
import VChart from "vue-echarts";
import { CanvasRenderer } from "echarts/renderers";
import { BarChart } from "echarts/charts";
import { GridComponent, TooltipComponent, TitleComponent } from "echarts/components";
import cubejsApi from "@/util/cubejsApi";
import type { EChartsOption } from "echarts";
import { useQuery } from "@tanstack/vue-query";

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent, TitleComponent]);

const { data: hotProductsTop10Data } = useQuery({
  queryKey: ["hot-products-top10"],
  queryFn: async () => {
    return await cubejsApi.load({
      measures: ["sale_order_items.quantity"],
      dimensions: ["products.name"],
      order: { "sale_order_items.quantity": "desc" },
      limit: 10
    });
  }
});

const chartOption = computed((): EChartsOption | null => {
  if (!hotProductsTop10Data.value) return null;

  const raw = hotProductsTop10Data.value.rawData();
  const xAxisData = raw.map((d) => d["products.name"] as string);
  const yAxisData = raw.map((d) => d["sale_order_items.quantity"] as number);

  return {
    title: {
      text: "📈 商品热销 Top 10",
      left: "center",
      textStyle: {
        fontSize: 18,
        fontWeight: "bold"
      }
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow"
      }
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: xAxisData,
      axisLabel: {
        rotate: 30,
        fontSize: 12
      }
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        name: "销售数量",
        type: "bar",
        data: yAxisData,
        barWidth: "50%",
        itemStyle: {
          borderRadius: [6, 6, 0, 0], // 柱状圆角
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
        },
        emphasis: {
          itemStyle: {
            shadowColor: "rgba(0, 0, 0, 0.3)",
            shadowBlur: 10
          }
        }
      }
    ]
  };
});
</script>
