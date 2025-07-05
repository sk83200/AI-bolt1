<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Chart, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, BarElement, LineController } from 'chart.js'
import annotationPlugin from 'chartjs-plugin-annotation'

Chart.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  LineController,
  BarElement,
  Title,
  Tooltip,
  Legend,
  annotationPlugin
)

// Props
const props = withDefaults(defineProps<{
  showAlerts?: boolean
}>(), {
  showAlerts: true
})

const chartCanvas = ref<HTMLCanvasElement | null>(null)
const chart = ref<Chart | null>(null)
const timeframe = ref('1M')

// Set alert configuration (visible based on prop)
const alerts = [
  {
    id: 'alert-1',
    type: 'line',
    label: {
      display: true,
      content: 'Buy Signal',
      position: 'start',
      backgroundColor: 'rgba(75, 192, 192, 0.8)',
      color: 'white',
      font: { weight: 'bold' }
    },
    yMin: 57000,
    yMax: 57000,
    borderColor: 'rgba(75, 192, 192, 0.8)',
    borderWidth: 2,
    borderDash: [5, 5]
  },
  {
    id: 'alert-2',
    type: 'line',
    label: {
      display: true,
      content: 'Stop Loss',
      position: 'start',
      backgroundColor: 'rgba(255, 99, 132, 0.8)',
      color: 'white',
      font: { weight: 'bold' }
    },
    yMin: 55000,
    yMax: 55000,
    borderColor: 'rgba(255, 99, 132, 0.8)',
    borderWidth: 2,
    borderDash: [5, 5]
  }
]

// Sample performance data
const generatePerformanceData = (timeframe: string) => {
  // Dates for X axis
  let labels: string[] = []
  let portfolioData: number[] = []
  let benchmarkData: number[] = []
  
  const baseValue = 55000
  
  // Generate data based on timeframe
  if (timeframe === '1W') {
    labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri']
    portfolioData = [55200, 55800, 55500, 56400, 56800]
    benchmarkData = [55300, 55600, 55400, 55900, 56200]
  } else if (timeframe === '1M') {
    labels = ['Week 1', 'Week 2', 'Week 3', 'Week 4']
    portfolioData = [54000, 55200, 56300, 56800]
    benchmarkData = [54200, 54900, 55700, 56200]
  } else if (timeframe === '3M') {
    labels = ['Jan', 'Feb', 'Mar']
    portfolioData = [52000, 54000, 56800]
    benchmarkData = [51000, 53000, 56200]
  } else if (timeframe === '1Y') {
    labels = ['Q1', 'Q2', 'Q3', 'Q4']
    portfolioData = [48000, 51000, 54000, 56800]
    benchmarkData = [49000, 51500, 53500, 56200]
  } else {
    // YTD
    labels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov']
    portfolioData = [48000, 49500, 50200, 51500, 52300, 53400, 54200, 54800, 55500, 56200, 56800]
    benchmarkData = [48500, 49000, 49800, 51000, 51800, 52600, 53500, 54300, 55000, 55500, 56200]
  }
  
  return { labels, portfolioData, benchmarkData }
}

// Create or update chart
const initChart = () => {
  if (!chartCanvas.value) return
  
  const ctx = chartCanvas.value.getContext('2d')
  if (!ctx) return
  
  // Get data based on timeframe
  const { labels, portfolioData, benchmarkData } = generatePerformanceData(timeframe.value)
  
  // Destroy existing chart if it exists
  if (chart.value) {
    chart.value.destroy()
  }
  
  // Create new chart
  chart.value = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: 'Your Portfolio',
          data: portfolioData,
          borderColor: '#4F46E5', // primary-600
          backgroundColor: 'rgba(79, 70, 229, 0.1)',
          borderWidth: 2,
          fill: true,
          tension: 0.3,
          pointRadius: 3,
          pointHoverRadius: 6
        },
        {
          label: 'S&P 500',
          data: benchmarkData,
          borderColor: '#9CA3AF', // gray-400
          backgroundColor: 'rgba(156, 163, 175, 0.1)',
          borderWidth: 2,
          borderDash: [5, 5],
          fill: false,
          tension: 0.3,
          pointRadius: 2,
          pointHoverRadius: 4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false
      },
      plugins: {
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            boxWidth: 6
          }
        },
        tooltip: {
          backgroundColor: 'rgba(17, 24, 39, 0.8)',
          padding: 12,
          titleFont: {
            size: 14
          },
          bodyFont: {
            size: 13
          },
          callbacks: {
            label: (context) => {
              let label = context.dataset.label || '';
              if (label) {
                label += ': ';
              }
              if (context.parsed.y !== null) {
                label += new Intl.NumberFormat('en-US', {
                  style: 'currency',
                  currency: 'USD'
                }).format(context.parsed.y);
              }
              return label;
            }
          }
        },
        annotation: props.showAlerts ? {
          annotations: alerts
        } : {}
      },
      scales: {
        x: {
          grid: {
            display: false
          }
        },
        y: {
          suggestedMin: Math.min(...portfolioData) * 0.98,
          suggestedMax: Math.max(...portfolioData) * 1.02,
          ticks: {
            callback: (value) => {
              return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
                minimumFractionDigits: 0
              }).format(Number(value));
            }
          }
        }
      }
    }
  })
}

// Change timeframe
const changeTimeframe = (newTimeframe: string) => {
  timeframe.value = newTimeframe
  initChart()
}

// Handle window resize
const handleResize = () => {
  if (chart.value) {
    chart.value.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chart.value) {
    chart.value.destroy()
  }
})
</script>

<template>
  <div>
    <!-- Timeframe selector -->
    <div class="flex space-x-2 mb-4">
      <button 
        v-for="period in ['1W', '1M', '3M', '1Y', 'YTD']" 
        :key="period"
        @click="changeTimeframe(period)"
        :class="[
          'px-3 py-1 text-sm rounded-md transition-colors',
          timeframe === period 
            ? 'bg-primary-100 text-primary-800 dark:bg-primary-900 dark:text-primary-200 font-medium' 
            : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800'
        ]"
      >
        {{ period }}
      </button>
    </div>
    
    <!-- Chart container -->
    <div class="relative h-72">
      <canvas ref="chartCanvas" class="w-full h-full"></canvas>
    </div>
  </div>
</template>