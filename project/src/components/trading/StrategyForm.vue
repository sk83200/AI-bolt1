<template>
  <div class="card">
    <h2 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Create Strategy</h2>
    
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <!-- Strategy Name -->
      <div>
        <label for="name" class="label">Strategy Name</label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          class="input"
          required
        />
      </div>
      
      <!-- Asset Class -->
      <div>
        <label for="assetClass" class="label">Asset Class</label>
        <select
          id="assetClass"
          v-model="form.assetClass"
          class="input"
          required
        >
          <option value="stocks">Stocks</option>
          <option value="crypto">Crypto</option>
          <option value="forex">Forex</option>
          <option value="commodities">Commodities</option>
        </select>
      </div>
      
      <!-- Timeframe -->
      <div>
        <label for="timeframe" class="label">Timeframe</label>
        <select
          id="timeframe"
          v-model="form.timeframe"
          class="input"
          required
        >
          <option value="1m">1 Minute</option>
          <option value="5m">5 Minutes</option>
          <option value="15m">15 Minutes</option>
          <option value="1h">1 Hour</option>
          <option value="4h">4 Hours</option>
          <option value="1d">1 Day</option>
        </select>
      </div>
      
      <!-- Entry Rules -->
      <div>
        <label for="entryRules" class="label">Entry Rules</label>
        <textarea
          id="entryRules"
          v-model="form.entryRules"
          rows="3"
          class="input"
          required
        ></textarea>
      </div>
      
      <!-- Exit Rules -->
      <div>
        <label for="exitRules" class="label">Exit Rules</label>
        <textarea
          id="exitRules"
          v-model="form.exitRules"
          rows="3"
          class="input"
          required
        ></textarea>
      </div>
      
      <!-- AI Assistance -->
      <div class="flex items-center">
        <input
          id="aiAssist"
          v-model="form.useAI"
          type="checkbox"
          class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label for="aiAssist" class="ml-2 block text-sm text-gray-900 dark:text-gray-300">
          Let AI assist in optimizing this strategy
        </label>
      </div>
      
      <!-- Error message -->
      <div v-if="error" class="text-error-500 text-sm">
        {{ error }}
      </div>
      
      <!-- Submit button -->
      <div class="flex justify-end space-x-3">
        <button
          type="button"
          class="btn btn-secondary"
          @click="resetForm"
        >
          Reset
        </button>
        <button
          type="submit"
          class="btn btn-primary"
          :disabled="isLoading"
        >
          {{ isLoading ? 'Creating...' : 'Create Strategy' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()
const isLoading = ref(false)
const error = ref('')

const form = reactive({
  name: '',
  assetClass: 'stocks',
  timeframe: '1d',
  entryRules: '',
  exitRules: '',
  useAI: false
})

const handleSubmit = async () => {
  if (!authStore.isProUser) {
    error.value = 'Strategy creation is a Pro feature. Please upgrade to continue.'
    return
  }
  
  error.value = ''
  isLoading.value = true
  
  try {
    const response = await axios.post('/api/strategies/create', {
      ...form,
      userId: authStore.user?.id
    })
    
    // Handle success
    console.log('Strategy created:', response.data)
    resetForm()
  } catch (err) {
    error.value = 'Failed to create strategy. Please try again.'
  } finally {
    isLoading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    name: '',
    assetClass: 'stocks',
    timeframe: '1d',
    entryRules: '',
    exitRules: '',
    useAI: false
  })
  error.value = ''
}
</script>