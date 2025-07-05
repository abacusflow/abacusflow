<template>
  <a-row justify="center" gutter="{16}">
    <a-col :span="24">
      <a-card style="height: 400px">
        <SaleTrendChart></SaleTrendChart>
      </a-card>
    </a-col>
    <a-col :span="24">
      <a-card style="height: 400px">
        <HotProductsTop10 />
      </a-card>
    </a-col>
    <a-col :span="12">
      <a-card style="height: 400px"> </a-card>
    </a-col>
    <a-col :span="12">
      <a-card style="height: 400px"> </a-card>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import HotProductsTop10 from "@/components/charts/HotProductsTop10Chart.vue";
import SaleTrendChart from "@/components/charts/SaleTrendChart.vue";
</script>
