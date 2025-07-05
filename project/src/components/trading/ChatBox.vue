<template>
  <div class="card h-full flex flex-col">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">AI Trading Assistant</h2>
    
    <!-- Chat messages -->
    <div class="flex-1 overflow-y-auto mb-4 space-y-4">
      <div 
        v-for="(message, index) in messages" 
        :key="index"
        :class="[
          'p-3 rounded-lg',
          message.role === 'assistant' 
            ? 'bg-primary-50 dark:bg-primary-900 text-gray-800 dark:text-gray-200' 
            : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200'
        ]"
      >
        <p class="whitespace-pre-line">{{ message.content }}</p>
      </div>
      
      <!-- Typing indicator -->
      <div v-if="isTyping" class="flex space-x-2 p-3">
        <div class="w-2 h-2 rounded-full bg-gray-400 animate-pulse"></div>
        <div class="w-2 h-2 rounded-full bg-gray-400 animate-pulse" style="animation-delay: 0.2s"></div>
        <div class="w-2 h-2 rounded-full bg-gray-400 animate-pulse" style="animation-delay: 0.4s"></div>
      </div>
    </div>
    
    <!-- Input area -->
    <div class="mt-auto">
      <form @submit.prevent="sendMessage" class="flex space-x-2">
        <input
          v-model="input"
          type="text"
          class="flex-1 input"
          placeholder="Describe your trading strategy or goal..."
          :disabled="isTyping"
        />
        <button 
          type="submit"
          class="btn btn-primary"
          :disabled="isTyping || !input.trim()"
        >
          Send
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const input = ref('')
const isTyping = ref(false)
const messages = ref<Array<{role: 'user' | 'assistant', content: string}>>([])

const sendMessage = async () => {
  if (!input.value.trim() || isTyping.value) return
  
  const userMessage = input.value
  messages.value.push({ role: 'user', content: userMessage })
  input.value = ''
  isTyping.value = true
  
  try {
    const response = await axios.post('/api/ai-chat', {
      message: userMessage,
      userId: authStore.user?.id
    })
    
    messages.value.push({
      role: 'assistant',
      content: response.data.message
    })
  } catch (error) {
    messages.value.push({
      role: 'assistant',
      content: 'Sorry, I encountered an error. Please try again.'
    })
  } finally {
    isTyping.value = false
  }
}
</script>