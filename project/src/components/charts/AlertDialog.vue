<script setup lang="ts">
import { ref, watch } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'

// Props
const props = defineProps<{
  show: boolean
  config: {
    symbol: string
    price: number
    direction: string
    message: string
  }
}>()

// Emits
const emit = defineEmits(['close', 'save'])

// Form state
const formState = ref({ ...props.config })

// Watch for config changes
watch(() => props.config, (newConfig) => {
  formState.value = { ...newConfig }
})

// Handle save
const handleSave = () => {
  // Validate
  if (!formState.value.price || !formState.value.message) {
    return
  }
  
  emit('save', { ...formState.value })
}

// Close modal
const close = () => {
  emit('close')
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Backdrop -->
    <div 
      class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
      @click="close"
    ></div>
    
    <!-- Modal dialog -->
    <div class="flex items-center justify-center min-h-screen p-4">
      <div 
        class="bg-white dark:bg-gray-800 rounded-lg overflow-hidden shadow-xl transform transition-all 
               w-full max-w-md mx-auto"
      >
        <!-- Header -->
        <div class="bg-primary-50 dark:bg-primary-900 px-6 py-4 flex items-center justify-between">
          <h3 class="text-lg font-medium text-primary-900 dark:text-primary-100">
            Create Price Alert
          </h3>
          <button 
            @click="close" 
            class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
          >
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>
        
        <!-- Body -->
        <div class="px-6 py-4">
          <form @submit.prevent="handleSave" class="space-y-4">
            <!-- Symbol -->
            <div>
              <label for="symbol" class="label">Symbol</label>
              <input
                id="symbol"
                type="text"
                v-model="formState.symbol"
                class="input bg-gray-100 dark:bg-gray-700"
                readonly
              />
            </div>
            
            <!-- Price -->
            <div>
              <label for="price" class="label">Alert Price</label>
              <input
                id="price"
                type="number"
                v-model="formState.price"
                step="0.01"
                class="input"
                required
              />
            </div>
            
            <!-- Direction -->
            <div>
              <label for="direction" class="label">Alert Direction</label>
              <select
                id="direction"
                v-model="formState.direction"
                class="input"
              >
                <option value="above">Price rises above</option>
                <option value="below">Price falls below</option>
              </select>
            </div>
            
            <!-- Message -->
            <div>
              <label for="message" class="label">Alert Message</label>
              <input
                id="message"
                type="text"
                v-model="formState.message"
                class="input"
                placeholder="e.g., Buy signal triggered"
                required
              />
            </div>
          </form>
        </div>
        
        <!-- Footer -->
        <div class="bg-gray-50 dark:bg-gray-700 px-6 py-4 flex justify-end space-x-3">
          <button 
            @click="close" 
            class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium 
                   text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700"
          >
            Cancel
          </button>
          <button 
            @click="handleSave" 
            class="btn btn-primary"
          >
            Create Alert
          </button>
        </div>
      </div>
    </div>
  </div>
</template>