<template>
  <div class="h-full flex flex-col bg-white dark:bg-gray-800">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700">
      <h3 class="font-medium text-gray-900 dark:text-white flex items-center">
        <span class="w-2 h-2 rounded-full bg-success-500 mr-2"></span>
        AI Trading Assistant
      </h3>
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
          placeholder="Ask me about trading strategies..."
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
        AI assistant provides general information only, not financial advice.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

// State
const isTyping = ref(false)
const input = ref('')
const messages = ref([
  {
    role: 'assistant',
    content: 'Hello! I\'m your AI trading assistant. How can I help you today?'
  }
])
const chatContainer = ref<HTMLElement | null>(null)

// Send message
const sendMessage = async () => {
  if (input.value.trim() === '') return
  
  // Add user message
  messages.value.push({
    role: 'user',
    content: input.value
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
    // In a real app, this would call the backend AI service
    const response = await generateResponse(userQuery)
    
    // Add AI response
    messages.value.push({
      role: 'assistant',
      content: response
    })
  } catch (error) {
    console.error('Error getting AI response:', error)
    messages.value.push({
      role: 'assistant',
      content: 'I apologize, but I encountered an error. Please try again.'
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

// Generate response based on user input
const generateResponse = async (query: string): Promise<string> => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  const lowerQuery = query.toLowerCase()
  
  if (lowerQuery.includes('market') && lowerQuery.includes('trend')) {
    return 'Based on current data, the market is showing an upward trend with technology and healthcare sectors outperforming. However, be cautious about energy stocks which are facing headwinds from regulatory changes.'
  }
  
  if (lowerQuery.includes('buy') || lowerQuery.includes('sell')) {
    if (!authStore.isProUser) {
      return 'Detailed buy/sell recommendations are available to Pro users only. Would you like to upgrade to access this feature?'
    }
    
    if (lowerQuery.includes('apple') || lowerQuery.includes('aapl')) {
      return 'Apple (AAPL) is currently trading near its all-time high with strong momentum. Analyst price targets suggest a potential 5-10% upside in the next quarter, supported by their services ecosystem growth and upcoming product cycle.'
    }
    
    if (lowerQuery.includes('microsoft') || lowerQuery.includes('msft')) {
      return 'Microsoft (MSFT) has strong fundamentals with their cloud business (Azure) showing excellent growth. Their AI integration across product lines positions them well for the future.'
    }
    
    return 'When considering whether to buy or sell, it\'s important to look at both fundamentals and technical indicators. Would you like me to analyze a specific stock for you?'
  }
  
  if (lowerQuery.includes('portfolio') && (lowerQuery.includes('optimize') || lowerQuery.includes('improve'))) {
    if (!authStore.isProUser) {
      return 'Portfolio optimization suggestions are a Pro feature. Would you like to upgrade to access detailed portfolio analysis?'
    }
    
    return 'To optimize your portfolio, consider:\n\n1. Review your asset allocation\n2. Look for highly correlated assets\n3. Consider adding uncorrelated assets\n4. Implement tax-efficient rebalancing\n5. Review fee structures'
  }
  
  if (lowerQuery.includes('strategy') || lowerQuery.includes('strategies')) {
    const basicResponse = 'Common trading strategies include:\n\n• Value investing\n• Growth investing\n• Momentum trading\n• Dollar-cost averaging'
    
    if (!authStore.isProUser) {
      return basicResponse + '\n\nUpgrade to Pro for detailed strategy analysis and AI-powered recommendations.'
    }
    
    return basicResponse + '\n\nWould you like me to analyze which strategy might work best for your goals?'
  }
  
  return 'I can help you with market analysis, trading strategies, portfolio optimization, and technical analysis. Is there a specific area you\'d like to focus on?'
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