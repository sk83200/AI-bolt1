<template>
  <div class="space-y-6">
    <!-- Profile Settings -->
    <div class="bg-white dark:bg-gray-800 shadow rounded-lg">
      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Profile Settings</h3>
        <form @submit.prevent="updateProfile" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label for="name" class="label">Full Name</label>
              <input
                id="name"
                v-model="profileForm.name"
                type="text"
                class="input"
                required
              />
            </div>
            <div>
              <label for="email" class="label">Email Address</label>
              <input
                id="email"
                v-model="profileForm.email"
                type="email"
                class="input"
                required
              />
            </div>
          </div>
          
          <div>
            <label for="tradingExperience" class="label">Trading Experience</label>
            <select
              id="tradingExperience"
              v-model="profileForm.tradingExperience"
              class="input"
            >
              <option value="beginner">Beginner</option>
              <option value="intermediate">Intermediate</option>
              <option value="advanced">Advanced</option>
              <option value="professional">Professional</option>
            </select>
          </div>
          
          <div>
            <label for="preferredMarkets" class="label">Preferred Markets</label>
            <div class="grid grid-cols-2 gap-2">
              <label class="flex items-center space-x-2">
                <input
                  type="checkbox"
                  v-model="profileForm.preferredMarkets"
                  value="stocks"
                  class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span class="text-sm text-gray-700 dark:text-gray-300">Stocks</span>
              </label>
              <label class="flex items-center space-x-2">
                <input
                  type="checkbox"
                  v-model="profileForm.preferredMarkets"
                  value="crypto"
                  class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span class="text-sm text-gray-700 dark:text-gray-300">Crypto</span>
              </label>
              <label class="flex items-center space-x-2">
                <input
                  type="checkbox"
                  v-model="profileForm.preferredMarkets"
                  value="forex"
                  class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span class="text-sm text-gray-700 dark:text-gray-300">Forex</span>
              </label>
              <label class="flex items-center space-x-2">
                <input
                  type="checkbox"
                  v-model="profileForm.preferredMarkets"
                  value="commodities"
                  class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span class="text-sm text-gray-700 dark:text-gray-300">Commodities</span>
              </label>
            </div>
          </div>
          
          <div class="flex justify-end">
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="isUpdating"
            >
              {{ isUpdating ? 'Saving...' : 'Save Changes' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Subscription Settings -->
    <div class="bg-white dark:bg-gray-800 shadow rounded-lg">
      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Subscription</h3>
        <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 mb-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900 dark:text-white">
                Current Plan: {{ authStore.isProUser ? 'Pro' : 'Free' }}
              </p>
              <p class="text-sm text-gray-500 dark:text-gray-400">
                {{ authStore.isProUser ? 'Billed monthly' : 'Limited features' }}
              </p>
            </div>
            <button 
              v-if="!authStore.isProUser"
              @click="upgradeToPro"
              class="btn btn-primary"
            >
              Upgrade to Pro
            </button>
            <button 
              v-else
              @click="cancelSubscription"
              class="btn btn-error"
              :disabled="isCancelling"
            >
              {{ isCancelling ? 'Cancelling...' : 'Cancel Subscription' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Notification Settings -->
    <div class="bg-white dark:bg-gray-800 shadow rounded-lg">
      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Notifications</h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900 dark:text-white">Trade Alerts</p>
              <p class="text-sm text-gray-500 dark:text-gray-400">Get notified about trade signals</p>
            </div>
            <button 
              type="button"
              role="switch"
              :aria-checked="notificationSettings.tradeAlerts"
              @click="toggleNotification('tradeAlerts')"
              :class="[
                'relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
                notificationSettings.tradeAlerts ? 'bg-primary-600' : 'bg-gray-200 dark:bg-gray-700'
              ]"
            >
              <span
                :class="[
                  'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                  notificationSettings.tradeAlerts ? 'translate-x-5' : 'translate-x-0'
                ]"
              />
            </button>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900 dark:text-white">Market Updates</p>
              <p class="text-sm text-gray-500 dark:text-gray-400">Daily market analysis and news</p>
            </div>
            <button 
              type="button"
              role="switch"
              :aria-checked="notificationSettings.marketUpdates"
              @click="toggleNotification('marketUpdates')"
              :class="[
                'relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
                notificationSettings.marketUpdates ? 'bg-primary-600' : 'bg-gray-200 dark:bg-gray-700'
              ]"
            >
              <span
                :class="[
                  'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                  notificationSettings.marketUpdates ? 'translate-x-5' : 'translate-x-0'
                ]"
              />
            </button>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900 dark:text-white">AI Insights</p>
              <p class="text-sm text-gray-500 dark:text-gray-400">AI-generated trading opportunities</p>
            </div>
            <button 
              type="button"
              role="switch"
              :aria-checked="notificationSettings.aiInsights"
              @click="toggleNotification('aiInsights')"
              :class="[
                'relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
                notificationSettings.aiInsights ? 'bg-primary-600' : 'bg-gray-200 dark:bg-gray-700'
              ]"
            >
              <span
                :class="[
                  'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                  notificationSettings.aiInsights ? 'translate-x-5' : 'translate-x-0'
                ]"
              />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Security Settings -->
    <div class="bg-white dark:bg-gray-800 shadow rounded-lg">
      <div class="p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Security</h3>
        <form @submit.prevent="updatePassword" class="space-y-4">
          <div>
            <label for="currentPassword" class="label">Current Password</label>
            <input
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              type="password"
              class="input"
              required
            />
          </div>
          
          <div>
            <label for="newPassword" class="label">New Password</label>
            <input
              id="newPassword"
              v-model="passwordForm.newPassword"
              type="password"
              class="input"
              required
            />
          </div>
          
          <div>
            <label for="confirmPassword" class="label">Confirm New Password</label>
            <input
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              type="password"
              class="input"
              required
            />
          </div>
          
          <div class="flex justify-end">
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="isUpdatingPassword"
            >
              {{ isUpdatingPassword ? 'Updating...' : 'Update Password' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

const authStore = useAuthStore()

// State
const isUpdating = ref(false)
const isUpdatingPassword = ref(false)
const isCancelling = ref(false)

// Profile form
const profileForm = reactive({
  name: authStore.user?.name || '',
  email: authStore.user?.email || '',
  tradingExperience: 'beginner',
  preferredMarkets: [] as string[]
})

// Password form
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Notification settings
const notificationSettings = reactive({
  tradeAlerts: true,
  marketUpdates: true,
  aiInsights: true
})

// Methods
const updateProfile = async () => {
  isUpdating.value = true
  try {
    await axios.put('/api/user/profile', profileForm)
    // Update local user data
    if (authStore.user) {
      authStore.user.name = profileForm.name
      authStore.user.email = profileForm.email
    }
    alert('Profile updated successfully')
  } catch (error) {
    console.error('Error updating profile:', error)
    alert('Failed to update profile')
  } finally {
    isUpdating.value = false
  }
}

const updatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('New passwords do not match')
    return
  }
  
  isUpdatingPassword.value = true
  try {
    await axios.put('/api/user/password', {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    
    // Clear form
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    
    alert('Password updated successfully')
  } catch (error) {
    console.error('Error updating password:', error)
    alert('Failed to update password')
  } finally {
    isUpdatingPassword.value = false
  }
}

const toggleNotification = (setting: keyof typeof notificationSettings) => {
  notificationSettings[setting] = !notificationSettings[setting]
}

const upgradeToPro = () => {
  // Navigate to pricing page
  router.push('/pricing')
}

const cancelSubscription = async () => {
  if (!confirm('Are you sure you want to cancel your subscription?')) return
  
  isCancelling.value = true
  try {
    await axios.post('/api/subscription/cancel')
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Error cancelling subscription:', error)
    alert('Failed to cancel subscription')
  } finally {
    isCancelling.value = false
  }
}
</script>