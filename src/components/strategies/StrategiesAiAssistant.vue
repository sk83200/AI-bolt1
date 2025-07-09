<template>
  <div class="h-full bg-white dark:bg-gray-800 flex flex-col">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h3 class="font-medium text-gray-900 dark:text-white flex items-center">
          <span :class="['w-2 h-2 rounded-full mr-2', isGuest ? 'bg-warning-500' : 'bg-success-500']"></span>
          AI Strategy Assistant
        </h3>
        <div v-if="isGuest" class="text-xs text-warning-600 dark:text-warning-400">
          Guest Mode
        </div>
      </div>
    </div>
    
    <!-- Chat messages -->
    <div 
      ref="chatContainer"
      class="flex-1 overflow-y-auto p-4 space-y-4"
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
        
        <!-- Apply suggestion button for assistant messages -->
        <div v-if="message.role === 'assistant' && message.suggestion" class="mt-3">
          <button 
            @click="applySuggestion(message.suggestion)"
            :disabled="isGuest"
            :class="[
              'text-xs px-3 py-1 rounded-md font-medium',
              isGuest 
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                : 'bg-primary-600 hover:bg-primary-700 text-white'
            ]"
          >
            {{ isGuest ? 'Sign up to apply' : 'Apply to Strategy' }}
          </button>
        </div>
        
        <!-- Guest mode upgrade prompt -->
        <div v-if="message.showUpgrade && isGuest" class="mt-3 p-3 bg-primary-100 dark:bg-primary-800 rounded-lg">
          <p class="text-xs text-primary-800 dark:text-primary-200 mb-2">
            🔒 Full strategy customization requires an account
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
    <div class="p-4 border-t border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-end">
        <textarea
          v-model="input"
          @keydown="handleKeyPress"
          :placeholder="isGuest ? 'Ask about strategies... (Guest mode - sample responses)' : 'Describe your strategy or ask for suggestions...'"
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
        {{ isGuest ? 'Guest mode provides sample responses. Sign up for full AI capabilities.' : 'AI assistant provides general information only, not financial advice.' }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'

// Props
const props = defineProps<{
  isGuest: boolean
}>()

// Emits
const emit = defineEmits(['strategy-update'])

// State
const isTyping = ref(false)
const input = ref('')
const messages = ref([
  {
    role: 'assistant',
    content: props.isGuest 
      ? 'Hello! I\'m your AI strategy assistant in guest mode. I can provide sample strategies and general guidance. For full customization, please sign up!'
      : 'Hello! I\'m your AI strategy assistant. I can help you build, optimize, and analyze trading strategies. What kind of strategy would you like to create?',
    showUpgrade: false
  }
])
const chatContainer = ref<HTMLElement | null>(null)

// Send message
const sendMessage = async () => {
  if (input.value.trim() === '') return
  
  // Add user message
  messages.value.push({
    role: 'user',
    content: input.value,
    showUpgrade: false
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
    // Generate response
    const response = await generateResponse(userQuery)
    
    // Add AI response
    messages.value.push(response)
  } catch (error) {
    console.error('Error getting AI response:', error)
    messages.value.push({
      role: 'assistant',
      content: 'I apologize, but I encountered an error. Please try again.',
      showUpgrade: false
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

// Generate response based on user input and guest status
const generateResponse = async (query: string): Promise<any> => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1200))
  
  const lowerQuery = query.toLowerCase()
  
  if (props.isGuest) {
    return generateGuestResponse(lowerQuery)
  } else {
    return generateFullResponse(lowerQuery)
  }
}

// Generate guest mode responses
const generateGuestResponse = (query: string): any => {
  if (query.includes('momentum') || query.includes('trend')) {
    return {
      role: 'assistant',
      content: `Here's a sample momentum strategy template:

**Momentum Strategy Framework**
• Entry: RSI > 70, Price > 20-day MA
• Exit: RSI < 30 or 5% stop loss
• Position size: 2% risk per trade
• Timeframe: Daily

This is a basic template. For full customization and backtesting, please sign up.`,
      showUpgrade: true,
      suggestion: null
    }
  }
  
  if (query.includes('mean reversion') || query.includes('oversold')) {
    return {
      role: 'assistant',
      content: `Here's a sample mean reversion strategy:

**Mean Reversion Strategy**
• Entry: RSI < 30, Price below 20-day MA
• Exit: RSI > 70 or 3% profit target
• Stop loss: 2%
• Best for: Range-bound markets

Sign up to customize parameters and run backtests.`,
      showUpgrade: true,
      suggestion: null
    }
  }
  
  return {
    role: 'assistant',
    content: `I can help you explore different strategy types:

• **Momentum** - Follow price trends
• **Mean Reversion** - Buy oversold, sell overbought
• **Breakout** - Trade price breakouts
• **Pairs Trading** - Relative value strategies

Tell me which interests you, and I'll provide a sample template. For full features, please sign up!`,
    showUpgrade: false,
    suggestion: null
  }
}

// Generate full responses for authenticated users
const generateFullResponse = (query: string): any => {
  if (query.includes('momentum')) {
    return {
      role: 'assistant',
      content: `I'll help you build a momentum strategy. Here are the key components:

**Entry Conditions:**
• RSI(14) > 60
• Price > 20-day SMA
• Volume > 1.5x average
• ADX > 25 (trend strength)

**Exit Conditions:**
• RSI < 40
• Price < 20-day SMA
• 5% stop loss
• 10% profit target

Would you like me to apply these settings to your strategy form?`,
      showUpgrade: false,
      suggestion: {
        type: 'momentum',
        entryRules: {
          conditions: ['RSI > 60', 'Price > SMA20', 'Volume > 1.5x avg'],
          indicators: ['RSI', 'SMA', 'Volume', 'ADX']
        },
        exitRules: {
          profitTarget: 10,
          stopLoss: 5,
          conditions: ['RSI < 40', 'Price < SMA20']
        }
      }
    }
  }
  
  if (query.includes('optimize') || query.includes('improve')) {
    return {
      role: 'assistant',
      content: `I can help optimize your strategy. Here are some suggestions:

**Risk Management Improvements:**
• Reduce position size during high volatility
• Add correlation filters
• Implement dynamic stop losses

**Entry/Exit Enhancements:**
• Add volume confirmation
• Use multiple timeframe analysis
• Implement trailing stops

Would you like me to apply these optimizations?`,
      showUpgrade: false,
      suggestion: {
        riskManagement: {
          maxRiskPerTrade: 1.5,
          maxPositions: 3,
          volatilityAdjustment: true
        },
        exitRules: {
          useTrailingStop: true,
          trailingDistance: 2
        }
      }
    }
  }
  
  return {
    role: 'assistant',
    content: `I can help you with:

• **Strategy Creation** - Build new strategies from scratch
• **Optimization** - Improve existing strategies
• **Risk Management** - Add protective measures
• **Backtesting** - Test historical performance
• **Parameter Tuning** - Find optimal settings

What would you like to work on?`,
    showUpgrade: false,
    suggestion: null
  }
}

// Apply suggestion to strategy
const applySuggestion = (suggestion: any) => {
  if (props.isGuest) return
  
  emit('strategy-update', suggestion)
  
  messages.value.push({
    role: 'assistant',
    content: 'Great! I\'ve applied those settings to your strategy form. You can review and modify them as needed.',
    showUpgrade: false
  })
}

// Scroll chat to bottom
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
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