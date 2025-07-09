<template>
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
        <!-- Chat Messages -->
        <div class="h-96 overflow-y-auto p-6 space-y-4">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="[
              'max-w-[85%] p-4 rounded-2xl transition-all duration-300',
              message.role === 'assistant' 
                ? 'bg-primary-50 dark:bg-primary-900 text-gray-800 dark:text-gray-200 mr-auto' 
                : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 ml-auto'
            ]"
          >
            <p class="text-sm whitespace-pre-line">{{ message.content }}</p>
            
            <!-- Login prompt for guest responses -->
            <div v-if="message.showLoginPrompt" class="mt-4 p-3 bg-primary-100 dark:bg-primary-800 rounded-lg">
              <p class="text-xs text-primary-800 dark:text-primary-200 mb-2">
                📌 To customize and backtest this strategy with real data, please log in.
              </p>
              <div class="flex space-x-2">
                <button 
                  @click="$emit('login')"
                  class="text-xs bg-primary-600 hover:bg-primary-700 text-white px-3 py-1 rounded-md font-medium"
                >
                  Sign Up Free
                </button>
                <button 
                  @click="$router.push('/login')"
                  class="text-xs text-primary-600 hover:text-primary-700 dark:text-primary-400 px-3 py-1"
                >
                  Log In
                </button>
              </div>
            </div>
          </div>
          
          <!-- Typing indicator -->
          <div 
            v-if="isTyping"
            class="max-w-[85%] p-4 rounded-2xl bg-primary-50 dark:bg-primary-900 text-gray-800 dark:text-gray-200 mr-auto"
          >
            <div class="flex space-x-1">
              <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse"></div>
              <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse" style="animation-delay: 0.2s"></div>
              <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse" style="animation-delay: 0.4s"></div>
            </div>
          </div>
        </div>
        
        <!-- Input Area -->
        <div class="border-t border-gray-200 dark:border-gray-700 p-6">
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
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// Emits
const emit = defineEmits(['engage', 'login'])

// State
const isTyping = ref(false)
const input = ref('')
const messages = ref([
  {
    role: 'assistant',
    content: 'Hello! I\'m your AI trading assistant. Describe the trading strategy you\'d like to create, and I\'ll help you build it step by step.',
    showLoginPrompt: false
  }
])

// Quick prompts for inspiration
const quickPrompts = [
  'Momentum strategy for tech stocks',
  'Low-risk dividend portfolio',
  'Crypto swing trading strategy',
  'Market volatility hedge'
]

// Send message
const sendMessage = async () => {
  if (input.value.trim() === '') return
  
  // Add user message
  messages.value.push({
    role: 'user',
    content: input.value,
    showLoginPrompt: false
  })
  
  // Clear input and emit engagement
  const userQuery = input.value
  input.value = ''
  emit('engage')
  
  // Show typing indicator
  isTyping.value = true
  
  try {
    // Generate response based on user input
    const response = await generateGuestResponse(userQuery)
    
    // Add AI response
    messages.value.push({
      role: 'assistant',
      content: response.content,
      showLoginPrompt: response.showLoginPrompt
    })
  } catch (error) {
    console.error('Error getting AI response:', error)
    messages.value.push({
      role: 'assistant',
      content: 'I apologize, but I encountered an error. Please try again.',
      showLoginPrompt: false
    })
  } finally {
    // Hide typing indicator
    isTyping.value = false
  }
}

// Use quick prompt
const useQuickPrompt = (prompt: string) => {
  input.value = prompt
  sendMessage()
}

// Generate guest response with templates
const generateGuestResponse = async (query: string): Promise<{content: string, showLoginPrompt: boolean}> => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1500))
  
  const lowerQuery = query.toLowerCase()
  
  // Rich strategy requests
  if (lowerQuery.includes('momentum') || lowerQuery.includes('tech') || lowerQuery.includes('return')) {
    return {
      content: `Here's a sample strategy that aligns with your goals:

**Tech Momentum Strategy**
This sample momentum strategy allocates equally to AAPL, TSLA, and NVDA when their 3-month returns exceed 12% and volatility is under 15%. It rebalances monthly and exits positions if drawdown exceeds 10%.

**Key Features:**
• Equal weight allocation (33.3% each)
• Monthly rebalancing
• 10% stop-loss protection
• Targets 15-20% annual returns

This is a general template. To customize parameters, run backtests with real data, and analyze performance metrics, please log in.`,
      showLoginPrompt: true
    }
  }
  
  if (lowerQuery.includes('dividend') || lowerQuery.includes('low risk')) {
    return {
      content: `Here's a sample low-risk dividend strategy:

**Dividend Growth Portfolio**
This conservative strategy focuses on dividend aristocrats with 10+ years of consecutive dividend increases. Allocates 60% to large-cap dividend stocks, 30% to REITs, and 10% to dividend ETFs.

**Key Features:**
• Focus on dividend aristocrats
• Quarterly rebalancing
• 5% maximum position size
• Target 8-12% annual returns

To customize this strategy with your risk tolerance and run detailed analysis, please log in.`,
      showLoginPrompt: true
    }
  }
  
  if (lowerQuery.includes('crypto') || lowerQuery.includes('bitcoin') || lowerQuery.includes('swing')) {
    return {
      content: `Here's a sample crypto swing trading strategy:

**Crypto Swing Strategy**
This strategy trades major cryptocurrencies (BTC, ETH, ADA) using RSI and moving average signals. Holds positions for 3-14 days targeting 5-15% moves.

**Key Features:**
• RSI(14) oversold/overbought signals
• 20/50 MA crossover confirmation
• 3% stop-loss, 10% profit target
• Maximum 3 concurrent positions

To backtest this strategy with real crypto data and optimize parameters, please log in.`,
      showLoginPrompt: true
    }
  }
  
  // General responses
  if (lowerQuery.includes('help') || lowerQuery.includes('how')) {
    return {
      content: `I can help you create various types of trading strategies:

• **Momentum strategies** - Buy rising stocks, sell falling ones
• **Mean reversion** - Buy oversold, sell overbought assets  
• **Trend following** - Follow long-term market trends
• **Dividend strategies** - Focus on income-generating assets
• **Risk management** - Portfolio protection and hedging

Just describe what kind of strategy you're interested in, and I'll provide a sample template. For full customization and backtesting, you'll need to log in.`,
      showLoginPrompt: false
    }
  }
  
  // Default response
  return {
    content: `I can help you build a trading strategy! Here are some popular approaches:

• **Growth/Momentum** - Target stocks with strong price momentum
• **Value/Contrarian** - Buy undervalued assets
• **Income/Dividend** - Focus on dividend-paying stocks
• **Sector Rotation** - Rotate between market sectors
• **Risk Parity** - Balance risk across asset classes

Tell me more about your investment goals, risk tolerance, or preferred asset classes, and I'll create a sample strategy for you.`,
    showLoginPrompt: false
  }
}

// Handle key press
const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}
</script>