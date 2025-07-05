<template>
  <v-chart :option="chartOption || {}" autoresize style="height: 400px" />
</template>

<script setup lang="ts">
import { computed } from "vue";
import VChart from "vue-echarts";
import cubejsApi from "@/plugin/cubejsApi";
import type { EChartsOption } from "echarts";
import { useQuery } from "@tanstack/vue-query";
import dayjs from "dayjs";

const { data: chatData } = useQuery({
  queryKey: ["salesTrendData"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["sale_order_item.revenue"],
      timeDimensions: [
        {
          dimension: "sale_order.order_date",
          granularity: "day"
        }
      ],
      dimensions: [],
      order: { "sale_order.order_date": "asc" }
    })
});

const chartOption = computed((): EChartsOption | null => {
  if (!chatData.value) return null;

  const raw = chatData.value.rawData();
  // 提取时间和销售金额数据
  const dates: string[] = [];
  const amounts: number[] = [];

  raw.forEach((row) => {
    const rawDate = row["sale_order.order_date"];
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD"); // 👈 格式化为 2025-06-27

    dates.push(formattedDate);
    amounts.push(Number(row["sale_order_item.revenue"]));
  });

  return {
    tooltip: {
      trigger: "axis",
    },
    xAxis: {
      type: "category",
      data: dates,
      name: "日期",
      axisLabel: {
        rotate: 45 // 防止日期太挤
      }
    },
    yAxis: {
      type: "value",
      name: "销售金额",
      axisLabel: {
        formatter: "{value} 元"
      }
    },
    series: [
      {
        type: "line", // 可替换为 "bar"
        data: amounts,
        smooth: true,
        name: "销售金额"
      }
    ],
    title: {
      text: "每日销售金额趋势",
      left: "center"
    },
    grid: {
      left: "10%",
      right: "10%",
      bottom: "15%",
      containLabel: true
    }
  };
});
</script>
