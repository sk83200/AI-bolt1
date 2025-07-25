<template>
  <div class="min-h-[calc(100vh-64px)]">
    <!-- Hero section -->
    <section class="py-12 px-4 md:py-20 bg-gradient-to-r from-primary-800 to-primary-900 dark:from-gray-800 dark:to-gray-900">
      <div class="max-w-7xl mx-auto">
        <div class="flex flex-col md:flex-row items-center">
          <!-- Text content -->
          <div class="md:w-1/2 text-center md:text-left mb-10 md:mb-0">
            <h1 class="text-4xl md:text-5xl font-bold text-white tracking-tight mb-4">
              Trade Smarter with <span class="text-accent-400">AI-Powered</span> Insights
            </h1>
            <p class="text-lg md:text-xl text-white/80 mb-8 max-w-xl">
              Harness the power of artificial intelligence to optimize your trading strategies and maximize returns in today's complex markets.
            </p>
            <div class="flex flex-col sm:flex-row justify-center md:justify-start space-y-4 sm:space-y-0 sm:space-x-4">
              <button 
                v-if="!authStore.isAuthenticated"
                @click="navigateToRegister" 
                class="btn bg-accent-500 hover:bg-accent-600 text-white px-6 py-3 rounded-md font-medium"
              >
                Get Started
              </button>
              <button 
                v-else
                @click="navigateToDashboard" 
                class="btn bg-accent-500 hover:bg-accent-600 text-white px-6 py-3 rounded-md font-medium"
              >
                Go to Dashboard
              </button>
              <button 
                v-if="!authStore.isAuthenticated"
                @click="navigateToLogin" 
                class="btn bg-white hover:bg-gray-100 text-primary-800 px-6 py-3 rounded-md font-medium"
              >
                Log In
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

    <!-- Pricing section -->
    <section class="py-16 px-4 bg-gray-50 dark:bg-gray-800">
      <div class="max-w-7xl mx-auto">
        <h2 class="text-3xl font-bold text-center text-gray-900 dark:text-white mb-12">
          Choose Your Plan
        </h2>
        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          <!-- Free plan -->
          <div class="card">
            <div class="p-6">
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">Free</h3>
              <p class="text-4xl font-bold text-gray-900 dark:text-white mb-6">$0</p>
              <ul class="space-y-3 mb-6">
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Basic charting tools</span>
                </li>
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Limited market data</span>
                </li>
              </ul>
              <button class="btn btn-secondary w-full">Get Started</button>
            </div>
          </div>

          <!-- Pro plan -->
          <div class="card border-2 border-primary-500 transform scale-105">
            <div class="p-6">
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">Pro</h3>
              <p class="text-4xl font-bold text-gray-900 dark:text-white mb-6">$49</p>
              <ul class="space-y-3 mb-6">
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Advanced AI insights</span>
                </li>
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Real-time market data</span>
                </li>
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Custom strategies</span>
                </li>
              </ul>
              <button class="btn btn-primary w-full">Upgrade to Pro</button>
            </div>
          </div>

          <!-- Enterprise plan -->
          <div class="card">
            <div class="p-6">
              <h3 class="text-2xl font-bold text-gray-900 dark:text-white mb-4">Enterprise</h3>
              <p class="text-4xl font-bold text-gray-900 dark:text-white mb-6">Custom</p>
              <ul class="space-y-3 mb-6">
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Custom solutions</span>
                </li>
                <li class="flex items-center">
                  <CheckIcon class="h-5 w-5 text-success-500 mr-2" />
                  <span>Dedicated support</span>
                </li>
              </ul>
              <button class="btn btn-secondary w-full">Contact Sales</button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChartBarIcon, CurrencyDollarIcon, LightBulbIcon, CheckIcon } from '@heroicons/vue/24/outline'
import { useAuthStore } from '../stores/auth'
import TradingChart from '../components/charts/TradingChart.vue'

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

const navigateToLogin = () => {
  router.push('/login')
}

const navigateToRegister = () => {
  router.push('/register')
}

const navigateToDashboard = () => {
  router.push('/dashboard')
}
</script>