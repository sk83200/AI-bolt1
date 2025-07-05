<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { CurrencyDollarIcon, ChartBarIcon, ClockIcon, ArrowTrendingUpIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '../stores/auth'
import PortfolioSummary from '../components/dashboard/PortfolioSummary.vue'
import RecentActivity from '../components/dashboard/RecentActivity.vue'
import PerformanceChart from '../components/charts/PerformanceChart.vue'
import AiAssistant from '../components/ai/AiAssistant.vue'

const authStore = useAuthStore()
const isLoading = ref(true)
const showAssistant = ref(false)

// Portfolio stats
const stats = ref([
  { 
    name: 'Portfolio Value', 
    value: '$56,271.89', 
    change: '+2.5%', 
    isPositive: true,
    icon: CurrencyDollarIcon 
  },
  { 
    name: 'Today\'s Change', 
    value: '+$876.12', 
    change: '+1.6%', 
    isPositive: true,
    icon: ArrowTrendingUpIcon 
  },
  { 
    name: 'Open Positions', 
    value: '8', 
    change: '-1', 
    isPositive: false,
    icon: ChartBarIcon 
  },
  { 
    name: 'Last Update', 
    value: 'Just now', 
    change: '', 
    isPositive: true,
    icon: ClockIcon 
  }
])

onMounted(() => {
  // Simulate loading data
  setTimeout(() => {
    isLoading.value = false
  }, 1000)
})

const toggleAssistant = () => {
  showAssistant.value = !showAssistant.value
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
      <button
        @click="toggleAssistant"
        class="btn btn-primary flex items-center"
      >
        <span v-if="!showAssistant">AI Assistant</span>
        <span v-else>Close Assistant</span>
      </button>
    </div>

    <!-- Skeleton loader while loading -->
    <div v-if="isLoading" class="animate-pulse">
      <!-- Stats skeleton -->
      <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-6">
        <div v-for="i in 4" :key="i" class="card h-24"></div>
      </div>
      
      <!-- Chart skeleton -->
      <div class="card h-80 mb-6"></div>
      
      <!-- Bottom section skeleton -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card h-96"></div>
        <div class="card h-96"></div>
      </div>
    </div>

    <!-- Dashboard content -->
    <div v-else>
      <!-- Stats overview -->
      <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-6">
        <div 
          v-for="(stat, index) in stats" 
          :key="index" 
          class="card flex items-start hover:shadow-lg transition-all duration-200"
        >
          <div class="p-3 rounded-full bg-primary-100 dark:bg-primary-900 mr-4">
            <component 
              :is="stat.icon" 
              class="h-6 w-6 text-primary-700 dark:text-primary-300"
            />
          </div>
          <div>
            <p class="text-sm font-medium text-gray-500 dark:text-gray-400">{{ stat.name }}</p>
            <p class="text-2xl font-semibold text-gray-900 dark:text-white">{{ stat.value }}</p>
            <p v-if="stat.change" 
               :class="[
                 'text-sm', 
                 stat.isPositive ? 'text-success-500' : 'text-error-500'
               ]"
            >
              {{ stat.change }}
            </p>
          </div>
        </div>
      </div>

      <!-- Portfolio performance chart -->
      <div class="card mb-6">
        <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
          Portfolio Performance
        </h2>
        <PerformanceChart />
      </div>
      
      <!-- Bottom grid with portfolio and activity -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <PortfolioSummary />
        <RecentActivity />
      </div>
    </div>

    <!-- AI Assistant Sidebar -->
    <AiAssistant 
      v-if="showAssistant" 
      @close="showAssistant = false" 
    />
  </div>
</template>