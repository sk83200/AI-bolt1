<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { Chart, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, TimeScale, BarElement, LineController } from 'chart.js'
import annotationPlugin from 'chartjs-plugin-annotation'

Chart.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  LineController,
  Title,
  Tooltip,
  Legend,
  TimeScale,
  annotationPlugin
)

// Props
const props = defineProps<{
  symbol: string
}>()

const chartCanvas = ref<HTMLCanvasElement | null>(null)
const chart = ref<Chart | null>(null)
const viewType = ref('candle')
const alerts = ref([
  {
    id: 'buy-alert',
    symbol: 'AAPL',
    price: 195.50,
    direction: 'above',
    enabled: true,
    message: 'Buy signal triggered'
  },
  {
    id: 'sell-alert',
    symbol: 'AAPL',
    price: 201.75,
    direction: 'above',
    enabled: true,
    message: 'Sell target reached'
  },
  {
    id: 'stop-loss',
    symbol: 'AAPL',
    price: 192.00,
    direction: 'below',
    enabled: true,
    message: 'Stop loss triggered'
  }
])

// Generate random prices for demo
const generatePrices = (base: number, days: number) => {
  const prices = []
  let currentPrice = base
  
  for (let i = 0; i < days; i++) {
    // Random price movement
    const change = (Math.random() - 0.5) * 2 * base * 0.01
    currentPrice += change
    currentPrice = parseFloat(currentPrice.toFixed(2))
    
    // Ensure minimum price
    if (currentPrice < base * 0.8) {
      currentPrice = base * 0.8
    }
    
    prices.push(currentPrice)
  }
  
  return prices
}

// Create or update chart
const initChart = () => {
  if (!chartCanvas.value) return
  
  const ctx = chartCanvas.value.getContext('2d')
  if (!ctx) return
  
  // Generate data based on symbol
  const basePrice = props.symbol === 'AAPL' ? 198.11 :
                   props.symbol === 'MSFT' ? 297.75 :
                   props.symbol === 'GOOGL' ? 2137.80 : 100.00
  
  const days = 30
  const dates = []
  const today = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(today.getDate() - i)
    dates.push(date.toISOString().split('T')[0])
  }
  
  const prices = generatePrices(basePrice, days)
  
  // Get configured alerts for this symbol
  const symbolAlerts = alerts.value.filter(alert => alert.symbol === props.symbol && alert.enabled)
  
  // Set up annotations for alerts
  const annotations = {}
  
  symbolAlerts.forEach(alert => {
    annotations[alert.id] = {
      type: 'line',
      label: {
        display: true,
        content: alert.message,
        position: 'start',
        backgroundColor: alert.direction === 'above' ? 'rgba(75, 192, 192, 0.8)' : 'rgba(255, 99, 132, 0.8)',
        color: 'white',
        font: { weight: 'bold' }
      },
      yMin: alert.price,
      yMax: alert.price,
      borderColor: alert.direction === 'above' ? 'rgba(75, 192, 192, 0.8)' : 'rgba(255, 99, 132, 0.8)',
      borderWidth: 2,
      borderDash: [5, 5]
    }
  })
  
  // Destroy existing chart if it exists
  if (chart.value) {
    chart.value.destroy()
  }
  
  // Create new chart
  chart.value = new Chart(ctx, {
    type: 'line',
    data: {
      labels: dates,
      datasets: [
        {
          label: props.symbol,
          data: prices,
          borderColor: '#4F46E5', // primary-600
          backgroundColor: 'rgba(79, 70, 229, 0.1)',
          borderWidth: 2,
          fill: true,
          tension: 0.1,
          pointRadius: 0,
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
          display: false
        },
        tooltip: {
          backgroundColor: 'rgba(17, 24, 39, 0.8)',
          padding: 12,
          titleFont: {
            size: 14,
            weight: '600'
          },
          bodyFont: {
            size: 13
          },
          callbacks: {
            label: (context) => {
              let label = props.symbol || '';
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
        annotation: {
          annotations
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            color: '#E5E7EB', // gray-200
            font: {
              size: 12,
              weight: '500'
            },
            maxTicksLimit: 10
          }
        },
        y: {
          position: 'right',
          grid: {
            display: false
          },
          ticks: {
            color: '#E5E7EB', // gray-200
            font: {
              size: 12,
              weight: '500'
            },
            padding: 8,
            callback: (value) => {
              return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
                minimumFractionDigits: 2
              }).format(Number(value));
            }
          }
        }
      }
    }
  })
}

// Handle window resize
const handleResize = () => {
  if (chart.value) {
    chart.value.resize()
  }
}

// Watch for symbol changes
watch(() => props.symbol, () => {
  initChart()
})

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
    <!-- Chart container -->
    <div class="relative h-72">
      <canvas ref="chartCanvas" class="w-full h-full"></canvas>
    </div>
    
    <!-- Alert indicators -->
    <div class="mt-4 flex flex-wrap gap-2">
      <div 
        v-for="alert in alerts.filter(a => a.symbol === symbol && a.enabled)" 
        :key="alert.id"
        :class="[
          'px-3 py-1 text-xs font-medium rounded-full flex items-center',
          alert.direction === 'above'
            ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
            : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
        ]"
      >
        <span class="w-2 h-2 rounded-full mr-1" 
              :class="alert.direction === 'above' ? 'bg-green-500' : 'bg-red-500'"></span>
        <span>{{ alert.message }}: {{ new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(alert.price) }}</span>
      </div>
    </div>
  </div>
</template>