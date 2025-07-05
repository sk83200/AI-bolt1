<template>
  <div class="h-full flex flex-col bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
      <h3 class="font-medium text-gray-900 dark:text-white">Messages & Alerts</h3>
      <div class="flex items-center space-x-2">
        <button 
          @click="markAllRead"
          class="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400"
        >
          Mark all read
        </button>
        <button 
          @click="clearAll"
          class="text-sm text-gray-500 hover:text-gray-700 dark:text-gray-400"
        >
          Clear all
        </button>
      </div>
    </div>
    
    <!-- Messages list -->
    <div class="flex-1 overflow-y-auto">
      <div v-if="messages.length === 0" class="p-4 text-center text-gray-500 dark:text-gray-400">
        No new messages
      </div>
      
      <div v-else class="divide-y divide-gray-200 dark:divide-gray-700">
        <div 
          v-for="message in messages" 
          :key="message.id"
          :class="[
            'p-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors cursor-pointer',
            !message.read && 'bg-primary-50 dark:bg-primary-900'
          ]"
          @click="markAsRead(message.id)"
        >
          <!-- Message type indicator -->
          <div class="flex items-center mb-1">
            <span 
              :class="[
                'w-2 h-2 rounded-full mr-2',
                message.type === 'alert' ? 'bg-error-500' :
                message.type === 'notification' ? 'bg-warning-500' :
                'bg-success-500'
              ]"
            ></span>
            <span class="text-xs text-gray-500 dark:text-gray-400">
              {{ formatTimestamp(message.timestamp) }}
            </span>
          </div>
          
          <!-- Message content -->
          <p class="text-sm text-gray-900 dark:text-white">
            {{ message.content }}
          </p>
          
          <!-- Action buttons -->
          <div v-if="message.actions" class="mt-2 flex space-x-2">
            <button 
              v-for="action in message.actions"
              :key="action.label"
              @click.stop="handleAction(message.id, action.type)"
              class="text-xs px-2 py-1 rounded-md bg-gray-100 dark:bg-gray-600 
                     text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-500"
            >
              {{ action.label }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { formatDistanceToNow } from 'date-fns'

interface Message {
  id: number
  type: 'alert' | 'notification' | 'message'
  content: string
  timestamp: Date
  read: boolean
  actions?: Array<{
    type: string
    label: string
  }>
}

// Sample messages
const messages = ref<Message[]>([
  {
    id: 1,
    type: 'alert',
    content: 'AAPL price alert triggered: Above $190',
    timestamp: new Date(Date.now() - 1000 * 60 * 5), // 5 minutes ago
    read: false,
    actions: [
      { type: 'view', label: 'View Chart' },
      { type: 'dismiss', label: 'Dismiss' }
    ]
  },
  {
    id: 2,
    type: 'notification',
    content: 'New AI-generated trading strategy available',
    timestamp: new Date(Date.now() - 1000 * 60 * 30), // 30 minutes ago
    read: false,
    actions: [
      { type: 'view', label: 'View Strategy' }
    ]
  },
  {
    id: 3,
    type: 'message',
    content: 'Your portfolio rebalancing is complete',
    timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2 hours ago
    read: true
  }
])

// Format timestamp
const formatTimestamp = (date: Date): string => {
  return formatDistanceToNow(date, { addSuffix: true })
}

// Mark message as read
const markAsRead = (id: number) => {
  const message = messages.value.find(m => m.id === id)
  if (message) {
    message.read = true
  }
}

// Mark all messages as read
const markAllRead = () => {
  messages.value.forEach(message => {
    message.read = true
  })
}

// Clear all messages
const clearAll = () => {
  messages.value = []
}

// Handle message action
const handleAction = (messageId: number, actionType: string) => {
  switch (actionType) {
    case 'view':
      console.log('View action for message:', messageId)
      break
    case 'dismiss':
      messages.value = messages.value.filter(m => m.id !== messageId)
      break
    default:
      console.log('Unknown action:', actionType)
  }
}
</script>