<template>
  <v-chart :option="chartOption || {}" autoresize style="height: 400px" />
</template>

<script setup lang="ts">
import { computed } from "vue";
import VChart from "vue-echarts";
import { useQuery } from "@tanstack/vue-query";
import cubejsApi from "@/plugin/cubejsApi";
import type { EChartsOption } from "echarts";
import dayjs from "dayjs";

const currentMonth = dayjs().format("YYYY-MM");
const { data: chartData } = useQuery({
  queryKey: ["newVsReturningCustomers"],
  queryFn: () =>
    cubejsApi.load({
      measures: ["sale_order_item.revenue", "sale_order.count"],
      dimensions: ["customer.name", "customer.created_at"],
      timeDimensions: [
        {
          dimension: "sale_order.order_date",
          dateRange: "This month"
        }
      ]
    })
});

const chartOption = computed((): EChartsOption | null => {
  if (!chartData.value) return null;

  let newRevenue = 0;
  let oldRevenue = 0;
  let newOrders = 0;
  let oldOrders = 0;

  chartData.value.rawData().forEach((row) => {
    const created = String(row["customer.created_at"]);
    const revenue = Number(row["sale_order_item.revenue"] || 0);
    const orderCount = Number(row["sale_order.count"] || 0);

    if (created.startsWith(currentMonth)) {
      newRevenue += revenue;
      newOrders += orderCount;
    } else {
      oldRevenue += revenue;
      oldOrders += orderCount;
    }
  });

  return {
    title: {
      text: "本月新老客户销售对比",
      left: "center"
    },
    legend: {
      bottom: 10,
      data: ["新客户", "老客户"]
    },
    tooltip: {
      trigger: "item"
    },
    series: [
      {
        name: "销售金额",
        type: "pie",
        radius: ["40%", "60%"],
        center: ["30%", "50%"],
        data: [
          { name: "新客户", value: newRevenue },
          { name: "老客户", value: oldRevenue }
        ],
        label: {
          formatter: (params) =>
            `${params.name}: ${Math.round(params.percent || 0)}%\n${params.value}元`
        }
      },
      {
        name: "订单数量",
        type: "pie",
        radius: ["40%", "60%"],
        center: ["75%", "50%"],
        data: [
          { name: "新客户", value: newOrders },
          { name: "老客户", value: oldOrders }
        ],
        label: {
          formatter: (params) =>
            `${params.name}: ${Math.round(params.percent || 0)}%\n${params.value}单`
        }
      }
    ]
  };
});
</script>
