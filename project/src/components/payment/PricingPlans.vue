<template>
  <div class="py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-3xl mx-auto">
      <div class="text-center mb-12">
        <h2 class="text-3xl font-bold text-gray-900 dark:text-white">
          Choose Your Plan
        </h2>
        <p class="mt-4 text-lg text-gray-600 dark:text-gray-400">
          Get access to advanced features and AI-powered insights
        </p>
      </div>
      
      <div class="grid md:grid-cols-2 gap-8">
        <!-- Free Plan -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg overflow-hidden">
          <div class="px-6 py-8">
            <h3 class="text-2xl font-semibold text-gray-900 dark:text-white">Free</h3>
            <p class="mt-4 text-gray-600 dark:text-gray-400">Perfect for getting started</p>
            <p class="mt-8">
              <span class="text-4xl font-bold text-gray-900 dark:text-white">$0</span>
              <span class="text-gray-600 dark:text-gray-400">/month</span>
            </p>
            
            <ul class="mt-8 space-y-4">
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">View sample analytics</span>
              </li>
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Basic market data</span>
              </li>
              <li class="flex items-center opacity-50">
                <XMarkIcon class="h-5 w-5 text-error-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Custom forecasting</span>
              </li>
              <li class="flex items-center opacity-50">
                <XMarkIcon class="h-5 w-5 text-error-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Strategy creation & backtest</span>
              </li>
            </ul>
          </div>
          
          <div class="px-6 py-4 bg-gray-50 dark:bg-gray-700">
            <button 
              class="w-full btn btn-secondary"
              disabled
            >
              Current Plan
            </button>
          </div>
        </div>
        
        <!-- Pro Plan -->
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg overflow-hidden border-2 border-primary-500">
          <div class="px-6 py-8">
            <h3 class="text-2xl font-semibold text-gray-900 dark:text-white">Pro</h3>
            <p class="mt-4 text-gray-600 dark:text-gray-400">For serious traders</p>
            <p class="mt-8">
              <span class="text-4xl font-bold text-gray-900 dark:text-white">$49</span>
              <span class="text-gray-600 dark:text-gray-400">/month</span>
            </p>
            
            <ul class="mt-8 space-y-4">
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">All Free features</span>
              </li>
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Custom forecasting</span>
              </li>
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Strategy creation & backtest</span>
              </li>
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Portfolio suggestions</span>
              </li>
              <li class="flex items-center">
                <CheckIcon class="h-5 w-5 text-success-500" />
                <span class="ml-3 text-gray-600 dark:text-gray-400">Save/load strategies</span>
              </li>
            </ul>
          </div>
          
          <div class="px-6 py-4 bg-gray-50 dark:bg-gray-700">
            <button 
              v-if="!isProUser"
              @click="startSubscription"
              class="w-full btn btn-primary"
              :disabled="isLoading"
            >
              {{ isLoading ? 'Processing...' : 'Upgrade to Pro' }}
            </button>
            <button 
              v-else
              class="w-full btn btn-success"
              disabled
            >
              Current Plan
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Payment Modal -->
    <div 
      v-if="showPaymentModal"
      class="fixed inset-0 z-50 overflow-y-auto"
      aria-labelledby="payment-modal"
      role="dialog"
      aria-modal="true"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
        
        <div class="relative bg-white dark:bg-gray-800 rounded-lg max-w-md w-full p-6">
          <div class="absolute top-0 right-0 pt-4 pr-4">
            <button
              @click="closePaymentModal"
              class="text-gray-400 hover:text-gray-500 focus:outline-none"
            >
              <XMarkIcon class="h-6 w-6" />
            </button>
          </div>
          
          <div class="mt-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
              Complete Your Subscription
            </h3>
            
            <div id="card-element" class="mb-4"></div>
            <div id="card-errors" class="text-error-500 text-sm mb-4"></div>
            
            <button 
              @click="processPayment"
              class="w-full btn btn-primary"
              :disabled="isProcessing"
            >
              {{ isProcessing ? 'Processing...' : 'Pay $49' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { CheckIcon, XMarkIcon } from '@heroicons/vue/24/outline'
import { loadStripe } from '@stripe/stripe-js'
import axios from 'axios'

const authStore = useAuthStore()
const isLoading = ref(false)
const isProcessing = ref(false)
const showPaymentModal = ref(false)
const stripe = ref<any>(null)
const card = ref<any>(null)

const isProUser = computed(() => authStore.isProUser)

onMounted(async () => {
  // Initialize Stripe
  stripe.value = await loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY)
  
  if (stripe.value) {
    const elements = stripe.value.elements()
    card.value = elements.create('card')
    card.value.mount('#card-element')
    
    // Handle validation errors
    card.value.addEventListener('change', (event: any) => {
      const displayError = document.getElementById('card-errors')
      if (displayError) {
        displayError.textContent = event.error ? event.error.message : ''
      }
    })
  }
})

const startSubscription = () => {
  showPaymentModal.value = true
}

const closePaymentModal = () => {
  showPaymentModal.value = false
}

const processPayment = async () => {
  if (!stripe.value || !card.value) return
  
  isProcessing.value = true
  
  try {
    // Create payment method
    const { paymentMethod, error } = await stripe.value.createPaymentMethod({
      type: 'card',
      card: card.value
    })
    
    if (error) {
      throw new Error(error.message)
    }
    
    // Send to backend
    const response = await axios.post('/api/subscription/create', {
      paymentMethodId: paymentMethod.id,
      userId: authStore.user?.id
    })
    
    // Handle successful subscription
    await authStore.upgradeToPro()
    showPaymentModal.value = false
    
    // Show success message
    alert('Successfully upgraded to Pro!')
  } catch (error) {
    console.error('Payment failed:', error)
    const errorElement = document.getElementById('card-errors')
    if (errorElement) {
      errorElement.textContent = error.message
    }
  } finally {
    isProcessing.value = false
  }
}
</script>