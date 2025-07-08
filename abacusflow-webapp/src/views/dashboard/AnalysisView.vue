<template>
  <a-row justify="center">
    <a-col :span="24">
      <a-card style="height: 400px">
        <SaleTrendChart></SaleTrendChart>
      </a-card>
    </a-col>
    <a-col :span="24">
      <a-card style="height: 430px">
        <ProductPriceTrackingChart></ProductPriceTrackingChart>
      </a-card>
    </a-col>
    <a-col :span="24">
      <a-card style="height: 400px">
        <HotProductsTop10 />
      </a-card>
    </a-col>
    <a-col :span="12">
      <a-card style="height: 430px">
        <ProductCategorySalesPieChart></ProductCategorySalesPieChart>
      </a-card>
    </a-col>
    <a-col :span="12">
      <a-card style="height: 430px">
        <NewVsReturningChart></NewVsReturningChart>
      </a-card>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import HotProductsTop10 from "@/components/charts/HotProductsTop10Chart.vue";
import NewVsReturningChart from "@/components/charts/NewVsReturningChart.vue";
import ProductCategorySalesPieChart from "@/components/charts/ProductCategorySalesPieChart.vue";
import ProductPriceTrackingChart from "@/components/charts/ProductPriceTrackingChart.vue";
import SaleTrendChart from "@/components/charts/SaleTrendChart.vue";
</script>
