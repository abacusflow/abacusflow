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
      measures: ["sale_order_item.revenue", "sale_order_item.count"],
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

  const dates: string[] = [];
  const revenues: number[] = [];
  const counts: number[] = [];

  raw.forEach((row) => {
    const rawDate = row["sale_order.order_date"];
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD");

    dates.push(formattedDate);
    revenues.push(Number(row["sale_order_item.revenue"]));
    counts.push(Number(row["sale_order_item.count"]));
  });

  return {
    tooltip: {
      trigger: "axis"
    },
    legend: {
      data: ["销售金额", "订单数量"],
      top: 30 //  往下挪一点，避免覆盖标题
    },
    xAxis: {
      type: "category",
      data: dates,
      name: "日期",
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: "value",
        name: "销售金额",
        axisLabel: {
          formatter: "{value} 元"
        }
      },
      {
        type: "value",
        name: "订单数量",
        axisLabel: {
          formatter: "{value} 单"
        }
      }
    ],
    series: [
      {
        name: "销售金额",
        type: "line",
        data: revenues,
        smooth: true,
        yAxisIndex: 0
      },
      {
        name: "订单数量",
        type: "line",
        data: counts,
        smooth: true,
        yAxisIndex: 1 // 使用第二个 y 轴
      }
    ],
    title: {
      text: "每日销售趋势（金额 + 数量）",
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
