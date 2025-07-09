<template>
  <div class="min-h-[calc(100vh-64px)]">
    <!-- For non-authenticated users: Show AI Assistant Panel -->
    <div v-if="!authStore.isAuthenticated" class="h-[calc(100vh-64px)]">
      <div class="h-full flex items-center justify-center bg-gradient-to-br from-primary-50 to-secondary-50 dark:from-gray-900 dark:to-gray-800">
        <div class="max-w-4xl w-full mx-auto px-6">
          <!-- Welcome Section -->
          <div class="text-center mb-8">
            <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-100 dark:bg-primary-900 rounded-full mb-4">
              <svg class="w-8 h-8 text-primary-600 dark:text-primary-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
            </div>
            <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              Welcome to AI Trader
            </h1>
            <p class="text-lg text-gray-600 dark:text-gray-300 mb-6">
              Describe your trading strategy and I'll help you build it
            </p>
          </div>

          <!-- AI Chat Interface -->
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl overflow-hidden transition-all duration-500 hover:shadow-2xl">
            <!-- Input Area Only (No Chat History) -->
            <div class="p-6">
              <div class="flex items-end space-x-4">
                <div class="flex-1">
                  <textarea
                    v-model="input"
                    @keydown="handleKeyPress"
                    placeholder="Describe your trading strategy... (e.g., 'Create a momentum strategy for tech stocks with 20% annual returns')"
                    class="w-full p-4 border border-gray-300 dark:border-gray-600 rounded-xl focus:outline-none 
                           focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none
                           dark:bg-gray-700 dark:text-white transition-all duration-200"
                    rows="3"
                  ></textarea>
                </div>
                <button 
                  @click="sendMessage" 
                  class="p-4 rounded-xl bg-primary-600 hover:bg-primary-700 text-white transition-all duration-200 transform hover:scale-105"
                  :disabled="input.trim() === ''"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
                  </svg>
                </button>
              </div>
              
              <!-- Quick prompts -->
              <div class="mt-4 flex flex-wrap gap-2">
                <button 
                  v-for="prompt in quickPrompts"
                  :key="prompt"
                  @click="useQuickPrompt(prompt)"
                  class="text-xs bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 
                         text-gray-700 dark:text-gray-300 px-3 py-2 rounded-lg transition-colors"
                >
                  {{ prompt }}
                </button>
              </div>
            </div>
          </div>

          <!-- Feature Preview -->
          <div class="mt-8 text-center">
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-4">
              AI assistant provides general information only, not financial advice.
            </p>
            <div class="flex justify-center space-x-6 text-xs text-gray-400 dark:text-gray-500">
              <span>✨ AI Strategy Builder</span>
              <span>📊 Backtesting</span>
              <span>📈 Performance Analytics</span>
              <span>🔄 Portfolio Optimization</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- For authenticated users: Show landing page with chart -->
    <div v-else class="min-h-[calc(100vh-64px)]">
      <!-- Hero section -->
      <section class="py-12 px-4 md:py-20 bg-gradient-to-r from-primary-800 to-primary-900 dark:from-gray-800 dark:to-gray-900">
        <div class="max-w-7xl mx-auto">
          <div class="flex flex-col md:flex-row items-center">
            <!-- Text content -->
            <div class="md:w-1/2 text-center md:text-left mb-10 md:mb-0">
              <h1 class="text-4xl md:text-5xl font-bold text-white tracking-tight mb-4">
                Welcome back, <span class="text-accent-400">{{ authStore.user?.name }}</span>
              </h1>
              <p class="text-lg md:text-xl text-white/80 mb-8 max-w-xl">
                Continue building your AI-powered trading strategies and maximize your returns.
              </p>
              <div class="flex flex-col sm:flex-row justify-center md:justify-start space-y-4 sm:space-y-0 sm:space-x-4">
                <button 
                  @click="navigateToDashboard" 
                  class="btn bg-accent-500 hover:bg-accent-600 text-white px-6 py-3 rounded-md font-medium"
                >
                  Go to Dashboard
                </button>
                <button 
                  @click="navigateToTrading" 
                  class="btn bg-white hover:bg-gray-100 text-primary-800 px-6 py-3 rounded-md font-medium"
                >
                  Strategy Builder
                </button>
              </div>
            </div>
            
            <!-- Chart preview -->
            <div class="md:w-1/2">
              <TradingChart symbol="AAPL" class="rounded-lg shadow-2xl" />
            </div>
          </div>
        </div>
      </section>

      <!-- Features section -->
      <section class="py-12 px-4 bg-white dark:bg-gray-900">
        <div class="max-w-7xl mx-auto">
          <h2 class="text-3xl font-bold text-center text-gray-900 dark:text-white mb-12">
            Professional-Grade Trading Tools
          </h2>
          
          <div class="grid md:grid-cols-3 gap-8">
            <div 
              v-for="(feature, index) in features" 
              :key="index"
              class="card hover:shadow-lg transition-all duration-300"
            >
              <div class="flex flex-col items-center text-center">
                <div class="p-3 rounded-full bg-primary-100 dark:bg-primary-900 mb-4">
                  <component 
                    :is="feature.icon" 
                    class="h-8 w-8 text-primary-700 dark:text-primary-300"
                  />
                </div>
                <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  {{ feature.title }}
                </h3>
                <p class="text-gray-600 dark:text-gray-300">
                  {{ feature.description }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChartBarIcon, CurrencyDollarIcon, LightBulbIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '../stores/auth'
import TradingChart from '../components/charts/TradingChart.vue'

const router = useRouter()
const authStore = useAuthStore()

// AI Assistant state
const input = ref('')

// Quick prompts for inspiration
const quickPrompts = [
  'Momentum strategy for tech stocks',
  'Low-risk dividend portfolio',
  'Crypto swing trading strategy',
  'Market volatility hedge'
]

const features = [
  {
    icon: ChartBarIcon,
    title: 'Advanced Analytics',
    description: 'Gain insights with our powerful analytics tools. Monitor market trends and visualize your portfolio performance in real-time.'
  },
  {
    icon: CurrencyDollarIcon,
    title: 'Smart Trading',
    description: 'Execute trades with confidence using our intelligent trading platform enhanced by AI-driven recommendations.'
  },
  {
    icon: LightBulbIcon,
    title: 'AI Assistant',
    description: 'Get personalized trading suggestions from our AI assistant, helping you make informed decisions based on market data.'
  }
]

// Send message - enables guest mode and navigates to trading
const sendMessage = () => {
  if (input.value.trim() === '') return
  
  // Enable guest mode when user engages
  authStore.loginAsGuest()
  router.push('/trading')
}

// Use quick prompt
const useQuickPrompt = (prompt: string) => {
  input.value = prompt
  sendMessage()
}

// Handle key press
const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

const navigateToDashboard = () => {
  router.push('/dashboard')
}

const navigateToTrading = () => {
  router.push('/trading')
}
</script>