<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// Form data
const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref('Trader')
const isLoading = ref(false)
const error = ref('')

// Available roles
const roles = [
  { value: 'Trader', label: 'Trader' },
  { value: 'Analyst', label: 'Analyst' }
]

// Form submission
const handleSubmit = async () => {
  // Reset error
  error.value = ''
  
  // Form validation
  if (!name.value || !email.value || !password.value || !confirmPassword.value) {
    error.value = 'Please fill in all fields'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }
  
  if (password.value.length < 8) {
    error.value = 'Password must be at least 8 characters long'
    return
  }
  
  try {
    isLoading.value = true
    const success = await authStore.register(name.value, email.value, password.value, role.value)
    
    if (success) {
      router.push('/dashboard')
    } else {
      error.value = 'Registration failed. Please try again.'
    }
  } catch (err) {
    error.value = 'An error occurred. Please try again.'
    console.error(err)
  } finally {
    isLoading.value = false
  }
}

// Navigate to login
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white dark:bg-gray-800 p-8 rounded-lg shadow-md">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
          Create your account
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600 dark:text-gray-400">
          Or
          <button 
            @click="goToLogin"
            class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400 dark:hover:text-primary-300"
          >
            sign in to existing account
          </button>
        </p>
      </div>
      
      <form @submit.prevent="handleSubmit" class="mt-8 space-y-6">
        <!-- Error alert -->
        <div v-if="error" class="p-3 bg-red-100 text-red-700 rounded-md text-sm">
          {{ error }}
        </div>
        
        <div class="rounded-md space-y-4">
          <div>
            <label for="name" class="label">Full Name</label>
            <input
              id="name"
              v-model="name"
              type="text"
              autocomplete="name"
              required
              class="input"
              placeholder="Full Name"
            />
          </div>
          
          <div>
            <label for="email" class="label">Email address</label>
            <input
              id="email"
              v-model="email"
              type="email"
              autocomplete="email"
              required
              class="input"
              placeholder="Email address"
            />
          </div>
          
          <div>
            <label for="role" class="label">I am a</label>
            <select
              id="role"
              v-model="role"
              class="input"
            >
              <option v-for="option in roles" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
          
          <div>
            <label for="password" class="label">Password</label>
            <input
              id="password"
              v-model="password"
              type="password"
              autocomplete="new-password"
              required
              class="input"
              placeholder="Password (min. 8 characters)"
            />
          </div>
          
          <div>
            <label for="confirm-password" class="label">Confirm Password</label>
            <input
              id="confirm-password"
              v-model="confirmPassword"
              type="password"
              autocomplete="new-password"
              required
              class="input"
              placeholder="Confirm Password"
            />
          </div>
        </div>

        <div>
          <button
            type="submit"
            class="btn btn-primary w-full flex justify-center py-3"
            :disabled="isLoading"
          >
            <span v-if="isLoading" class="flex items-center">
              <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Processing...
            </span>
            <span v-else>Create Account</span>
          </button>
        </div>
      </form>
      
      <p class="mt-2 text-xs text-center text-gray-500 dark:text-gray-400">
        By signing up, you agree to our Terms of Service and Privacy Policy
      </p>
    </div>
  </div>
</template>