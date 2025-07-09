<template>
  <div class="h-full bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between">
        <h3 class="font-medium text-gray-900 dark:text-white flex items-center">
          <span class="w-2 h-2 rounded-full bg-warning-500 mr-2"></span>
          AI Assistant (Guest Mode)
        </h3>
        <div class="text-xs text-gray-500 dark:text-gray-400">
          Limited responses
        </div>
      </div>
    </div>
    
    <!-- Chat messages -->
    <div 
      ref="chatContainer"
      class="flex-1 overflow-y-auto p-4 space-y-4 h-[calc(100vh-200px)]"
    >
      <div 
        v-for="(message, index) in messages" 
        :key="index"
        :class="[
          'max-w-[85%] p-3 rounded-lg',
          message.role === 'assistant' 
            ? 'bg-primary-50 dark:bg-primary-900 text-gray-800 dark:text-gray-200 mr-auto' 
            : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 ml-auto'
        ]"
      >
        <p class="text-sm whitespace-pre-line">{{ message.content }}</p>
        
        <!-- Login prompt for guest responses -->
        <div v-if="message.showLoginPrompt" class="mt-3 p-3 bg-primary-100 dark:bg-primary-800 rounded-lg">
          <p class="text-xs text-primary-800 dark:text-primary-200 mb-2">
            📌 To customize and backtest this strategy with real data, please log in.
          </p>
          <div class="flex space-x-2">
            <button 
              @click="$router.push('/register')"
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
        class="max-w-[85%] p-3 rounded-lg bg-primary-50 dark:bg-primary-900 text-gray-800 dark:text-gray-200 mr-auto"
      >
        <div class="flex space-x-1">
          <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse"></div>
          <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse" style="animation-delay: 0.2s"></div>
          <div class="w-2 h-2 rounded-full bg-primary-400 dark:bg-primary-600 animate-pulse" style="animation-delay: 0.4s"></div>
        </div>
      </div>
    </div>
    
    <!-- Input area -->
    <div class="p-4 border-t border-gray-200 dark:border-gray-700">
      <div class="flex items-end">
        <textarea
          v-model="input"
          @keydown="handleKeyPress"
          placeholder="Ask about trading strategies... (Guest mode - limited responses)"
          class="flex-1 p-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none 
                 focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none
                 dark:bg-gray-700 dark:text-white"
          rows="2"
        ></textarea>
        <button 
          @click="sendMessage" 
          class="ml-2 p-3 rounded-full bg-primary-600 hover:bg-primary-700 text-white transition-colors"
          :disabled="input.trim() === ''"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
          </svg>
        </button>
      </div>
      <p class="mt-2 text-xs text-gray-500 dark:text-gray-400 text-center">
        Guest mode provides sample responses. Sign up for full AI capabilities.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'

// State
const isTyping = ref(false)
const input = ref('')
const messages = ref([
  {
    role: 'assistant',
    content: 'Hello! I\'m your AI trading assistant in guest mode. I can provide sample strategies and general guidance. For full features, please sign up!',
    showLoginPrompt: false
  }
])
const chatContainer = ref<HTMLElement | null>(null)

// Rate limiting for guest mode
const lastRequestTime = ref(0)
const requestCount = ref(0)
const maxRequestsPerMinute = 5

// Send message
const sendMessage = async () => {
  if (input.value.trim() === '') return
  
  // Rate limiting check
  const now = Date.now()
  if (now - lastRequestTime.value < 60000) {
    if (requestCount.value >= maxRequestsPerMinute) {
      messages.value.push({
        role: 'assistant',
        content: 'Rate limit reached. Please wait a moment or sign up for unlimited access.',
        showLoginPrompt: true
      })
      return
    }
  } else {
    requestCount.value = 0
  }
  
  requestCount.value++
  lastRequestTime.value = now
  
  // Add user message
  messages.value.push({
    role: 'user',
    content: input.value,
    showLoginPrompt: false
  })
  
  // Clear input
  const userQuery = input.value
  input.value = ''
  
  // Scroll to bottom
  await nextTick()
  scrollToBottom()
  
  // Show typing indicator
  isTyping.value = true
  
  try {
    // Generate guest response
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
    
    // Scroll to bottom
    nextTick(() => {
      scrollToBottom()
    })
  }
}

// Scroll chat to bottom
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// Generate guest response with templates
const generateGuestResponse = async (query: string): Promise<{content: string, showLoginPrompt: boolean}> => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  const lowerQuery = query.toLowerCase()
  
  // Template responses for common queries
  const templates = {
    momentum: {
      content: `Here's a sample momentum strategy:

**Tech Momentum Strategy**
This sample strategy allocates equally to AAPL, TSLA, and NVDA when their 3-month returns exceed 12% and volatility is under 15%. It rebalances monthly and exits positions if drawdown exceeds 10%.

**Key Features:**
• Equal weight allocation (33.3% each)
• Monthly rebalancing  
• 10% stop-loss protection
• Targets 15-20% annual returns`,
      showLoginPrompt: true
    },
    
    dividend: {
      content: `Here's a sample dividend strategy:

**Dividend Growth Portfolio**
This conservative strategy focuses on dividend aristocrats with 10+ years of consecutive dividend increases. Allocates 60% to large-cap dividend stocks, 30% to REITs, and 10% to dividend ETFs.

**Key Features:**
• Focus on dividend aristocrats
• Quarterly rebalancing
• 5% maximum position size
• Target 8-12% annual returns`,
      showLoginPrompt: true
    },
    
    crypto: {
      content: `Here's a sample crypto strategy:

**Crypto Swing Strategy**
This strategy trades major cryptocurrencies (BTC, ETH, ADA) using RSI and moving average signals. Holds positions for 3-14 days targeting 5-15% moves.

**Key Features:**
• RSI(14) oversold/overbought signals
• 20/50 MA crossover confirmation
• 3% stop-loss, 10% profit target
• Maximum 3 concurrent positions`,
      showLoginPrompt: true
    }
  }
  
  // Match query to templates
  if (lowerQuery.includes('momentum') || lowerQuery.includes('tech')) {
    return templates.momentum
  }
  
  if (lowerQuery.includes('dividend') || lowerQuery.includes('income')) {
    return templates.dividend
  }
  
  if (lowerQuery.includes('crypto') || lowerQuery.includes('bitcoin')) {
    return templates.crypto
  }
  
  if (lowerQuery.includes('return') && (lowerQuery.includes('20') || lowerQuery.includes('high'))) {
    return {
      content: `Here's a sample high-return strategy that aligns with your goals:

**Aggressive Growth Strategy**
This sample strategy targets 20%+ annual returns by focusing on high-growth stocks and momentum plays. Uses a combination of technical indicators and fundamental screening.

**Sample Allocation:**
• 40% Growth stocks (AMZN, GOOGL, MSFT)
• 30% Momentum plays (sector rotation)
• 20% Small-cap growth
• 10% Cash for opportunities

To customize this strategy and run backtests with real data, please log in.`,
      showLoginPrompt: true
    }
  }
  
  // Default response
  return {
    content: `I can help you explore different trading strategies! Here are some popular approaches:

• **Growth/Momentum** - Target stocks with strong price momentum
• **Value/Contrarian** - Buy undervalued assets  
• **Income/Dividend** - Focus on dividend-paying stocks
• **Sector Rotation** - Rotate between market sectors

Tell me more about your investment goals, and I'll provide a sample strategy template. For full customization and backtesting, you'll need to sign up.`,
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

onMounted(() => {
  scrollToBottom()
})
</script>