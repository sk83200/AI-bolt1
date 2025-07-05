<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Trading Strategy Builder</h1>
      <button 
        @click="openAlertDialog(currentSymbol)" 
        class="btn btn-accent"
      >
        Create Alert
      </button>
    </div>

    <!-- Strategy Builder with Property Sheet Layout -->
    <StrategyBuilder />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import StrategyBuilder from '../components/trading/StrategyBuilder.vue'

const currentSymbol = ref('AAPL')

const openAlertDialog = (symbol: string) => {
  // Implementation for opening alert dialog
  console.log('Opening alert dialog for:', symbol)
}
</script>