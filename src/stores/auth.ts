import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// User interface
interface User {
  id: string
  name: string
  email: string
  role: string
  isPro: boolean
  isReturning?: boolean
}

// Auth Store
export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const guestMode = ref(false)

  // Load initial state from localStorage
  const initializeState = () => {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    
    if (storedUser) {
      user.value = JSON.parse(storedUser)
      // Mark as returning user if they have stored data
      if (user.value) {
        user.value.isReturning = true
      }
    }
    if (storedToken) token.value = storedToken
  }
  
  // Initialize on store creation
  initializeState()

  // Computed
  const isAuthenticated = computed(() => !!token.value && !guestMode.value)
  const isProUser = computed(() => user.value?.isPro || false)
  const isReturningUser = computed(() => user.value?.isReturning || false)
  
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
        isPro: false,
        isReturning: !!localStorage.getItem('user') // Check if returning
      }
      const mockToken = 'demo-jwt-token'
      
      // Set state
      user.value = mockUser
      token.value = mockToken
      guestMode.value = false
      
      // Store in localStorage
      localStorage.setItem('user', JSON.stringify(mockUser))
      localStorage.setItem('token', mockToken)
      
      return true
    } catch (error) {
      console.error('Login failed', error)
      return false
    }
  }

  const register = async (name: string, email: string, password: string, role: string) => {
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 800))
      
      // Mock successful registration
      const mockUser: User = {
        id: '1',
        name: name,
        email: email,
        role: role,
        isPro: false,
        isReturning: false // New user
      }
      const mockToken = 'demo-jwt-token'
      
      // Set state
      user.value = mockUser
      token.value = mockToken
      guestMode.value = false
      
      // Store in localStorage
      localStorage.setItem('user', JSON.stringify(mockUser))
      localStorage.setItem('token', mockToken)
      
      return true
    } catch (error) {
      console.error('Registration failed', error)
      return false
    }
  }

  const loginAsGuest = () => {
    guestMode.value = true
    user.value = {
      id: 'guest',
      name: 'Guest User',
      email: '',
      role: 'Guest',
      isPro: false,
      isReturning: false
    }
    token.value = 'guest-token'
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
    guestMode.value = false
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  const hasRole = (roles: string | string[]) => {
    if (!user.value) return false
    const userRole = user.value.role
    if (Array.isArray(roles)) {
      return roles.includes(userRole)
    }
    return userRole === roles
  }
  
  return {
    user,
    token,
    guestMode,
    isAuthenticated,
    isProUser,
    isReturningUser,
    login,
    register,
    loginAsGuest,
    logout,
    upgradeToPro,
    hasRole
  }
})