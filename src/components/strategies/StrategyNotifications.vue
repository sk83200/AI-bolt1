<template>
  <div class="h-full flex flex-col bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <div class="p-3 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h4 class="text-sm font-medium text-gray-900 dark:text-white">Messages & Notifications</h4>
        <div class="flex items-center space-x-2">
          <button 
            @click="clearAll"
            :disabled="isGuest"
            class="text-xs text-gray-500 hover:text-gray-700 dark:text-gray-400 disabled:opacity-50"
          >
            Clear all
          </button>
        </div>
      </div>
    </div>
    
    <!-- Notifications list -->
    <div class="flex-1 overflow-y-auto">
      <div v-if="notifications.length === 0" class="p-4 text-center text-gray-500 dark:text-gray-400 text-sm">
        No notifications
      </div>
      
      <div v-else class="divide-y divide-gray-200 dark:divide-gray-700">
        <div 
          v-for="notification in notifications" 
          :key="notification.id"
          :class="[
            'p-3 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors',
            getNotificationBg(notification.type)
          ]"
        >
          <!-- Notification content -->
          <div class="flex items-start space-x-2">
            <div :class="['w-2 h-2 rounded-full mt-1.5 flex-shrink-0', getNotificationColor(notification.type)]"></div>
            <div class="flex-1 min-w-0">
              <p class="text-xs text-gray-900 dark:text-white">
                {{ notification.message }}
              </p>
              <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                {{ formatTimestamp(notification.timestamp) }}
              </p>
            </div>
            <button 
              v-if="!isGuest"
              @click="dismissNotification(notification.id)"
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            >
              <svg class="h-3 w-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Guest mode info -->
    <div v-if="isGuest" class="p-3 border-t border-gray-200 dark:border-gray-700 bg-warning-50 dark:bg-warning-900">
      <p class="text-xs text-warning-800 dark:text-warning-200">
        📌 Guest mode: Limited notification features. Sign up for full access.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatDistanceToNow } from 'date-fns'

// Props
const props = defineProps<{
  notifications: Array<{
    id: number
    type: 'info' | 'success' | 'warning' | 'error'
    message: string
    timestamp: Date
  }>
  isGuest: boolean
}>()

// Format timestamp
const formatTimestamp = (date: Date): string => {
  return formatDistanceToNow(date, { addSuffix: true })
}

// Get notification background color
const getNotificationBg = (type: string): string => {
  switch (type) {
    case 'success':
      return 'bg-green-50 dark:bg-green-900'
    case 'warning':
      return 'bg-yellow-50 dark:bg-yellow-900'
    case 'error':
      return 'bg-red-50 dark:bg-red-900'
    default:
      return 'bg-blue-50 dark:bg-blue-900'
  }
}

// Get notification dot color
const getNotificationColor = (type: string): string => {
  switch (type) {
    case 'success':
      return 'bg-green-500'
    case 'warning':
      return 'bg-yellow-500'
    case 'error':
      return 'bg-red-500'
    default:
      return 'bg-blue-500'
  }
}

// Dismiss notification
const dismissNotification = (id: number) => {
  if (props.isGuest) return
  
  const index = props.notifications.findIndex(n => n.id === id)
  if (index > -1) {
    props.notifications.splice(index, 1)
  }
}

// Clear all notifications
const clearAll = () => {
  if (props.isGuest) return
  
  props.notifications.splice(0)
}
</script>