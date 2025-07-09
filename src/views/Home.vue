<template>
  <div class="min-h-[calc(100vh-64px)]">
    <!-- Guest Mode: Show AI Assistant Panel -->
    <div v-if="!authStore.isAuthenticated" class="h-[calc(100vh-64px)]">
      <CenteredAiAssistant 
        @engage="handleGuestEngagement"
        @login="handleGuestLogin"
      />
    </div>

    <!-- Authenticated: Show landing page -->
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
import CenteredAiAssistant from '../components/ai/CenteredAiAssistant.vue'

const router = useRouter()
const authStore = useAuthStore()

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

const handleGuestEngagement = () => {
  // Enable guest mode when user engages
  authStore.loginAsGuest()
  router.push('/trading')
}

const handleGuestLogin = () => {
  router.push('/register')
}

const navigateToDashboard = () => {
  router.push('/dashboard')
}

const navigateToTrading = () => {
  router.push('/trading')
}
</script>