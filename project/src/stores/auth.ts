import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// User interface
interface User {
  id: string
  name: string
  email: string
  role: string
  isPro: boolean
}

// Auth Store
export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Load initial state from localStorage
  const initializeState = () => {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    
    if (storedUser) user.value = JSON.parse(storedUser)
    if (storedToken) token.value = storedToken
  }
  
  // Initialize on store creation
  initializeState()

  // Computed
  const isAuthenticated = computed(() => !!token.value)
  const isProUser = computed(() => user.value?.isPro || false)
  
  // Methods
  const login = async (email: string, password: string) => {
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 800))
      
      // Mock successful login
      const mockUser: User = {
        id: '1',
        name: 'Demo User',
        email: email,
        role: 'Trader',
        isPro: false
      }
      const mockToken = 'demo-jwt-token'
      
      // Set state
      user.value = mockUser
      token.value = mockToken
      
      // Store in localStorage
      localStorage.setItem('user', JSON.stringify(mockUser))
      localStorage.setItem('token', mockToken)
      
      return true
    } catch (error) {
      console.error('Login failed', error)
      return false
    }
  }
  
  const upgradeToPro = async () => {
    if (user.value) {
      user.value.isPro = true
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }
  
  const logout = async () => {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }
  
  return {
    user,
    token,
    isAuthenticated,
    isProUser,
    login,
    logout,
    upgradeToPro
  }
})