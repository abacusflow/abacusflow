<template>
  <a-row justify="center" gutter="{16}">
    <a-col :span="12">
      <a-card style="height: 400px">
        <HotProductsTop10 />
      </a-card>
    </a-col>
    <a-col :span="12">
      <a-card style="height: 400px">
        <SalesTrendChart></SalesTrendChart>
      </a-card>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import HotProductsTop10 from "@/components/charts/HotProductsTop10Chart.vue";
import SalesTrendChart from "@/components/charts/SalesTrendChart.vue";
</script>
