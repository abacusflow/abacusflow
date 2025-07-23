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

const { data: chartData } = useQuery({
  queryKey: ["salesTrendData"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["sale_order_item.revenue", "sale_order_item.count", "sale_order_item.profit"],
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
  if (!chartData.value) return null;

  const raw = chartData.value.rawData();

  const dates: string[] = [];
  const revenues: number[] = [];
  const counts: number[] = [];
  const profits: number[] = [];

  raw.forEach((row) => {
    const rawDate = row["sale_order.order_date"] as string | null;
    const formattedDate = dayjs(rawDate).format("YYYY-MM-DD");

    dates.push(formattedDate);
    revenues.push(Number(row["sale_order_item.revenue"]) || 0);
    counts.push(Number(row["sale_order_item.count"]) || 0);
    profits.push(Number(row["sale_order_item.profit"]) || 0);
  });

  return {
    title: {
      text: "每日销售趋势",
      left: "center"
    },
    tooltip: {
      trigger: "axis"
      // axisPointer: {
      //   type: "cross"
      // }
    },
    legend: {
      data: ["利润金额", "销售金额", "订单数量"],
      top: 30 // 往下挪一点，避免覆盖标题
    },
    grid: {
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: dates,
      name: "日期",
      nameGap: 30,

      axisLabel: {
        // rotate: 25
      },
      axisLine: {
        onZero: false // 👈 X 轴固定在底部，而不是跟随 Y=0
      }
    },
    yAxis: [
      {
        type: "value",
        name: "金额",
        position: "left",
        axisLabel: {
          formatter: "{value} 元"
        }
      },
      {
        type: "value",
        name: "订单数量",
        position: "right",
        min: 0,
        axisLabel: {
          formatter: "{value} 单"
        }
      }
    ],
    series: [
      {
        name: "利润金额",
        type: "line",
        data: profits,
        smooth: true,
        yAxisIndex: 0,
        itemStyle: {
          color: "#91cc75"
        }
      },
      {
        name: "销售金额",
        type: "line",
        data: revenues,
        smooth: true,
        yAxisIndex: 0,
        itemStyle: {
          color: "#5470c6"
        }
      },
      {
        name: "订单数量",
        type: "line",
        data: counts,
        smooth: true,
        yAxisIndex: 1, // 使用第二个 y 轴
        itemStyle: {
          color: "#fac858"
        }
      }
    ]
  };
});
</script>
