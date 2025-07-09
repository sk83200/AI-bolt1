import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Workspace state interface
interface WorkspaceState {
  strategyType: string
  formData: Record<string, any>
  assistantHistory: Array<{role: string, content: string}>
  lastModified: Date
}

// Workspace Store
export const useWorkspaceStore = defineStore('workspace', () => {
  // State
  const hasEngaged = ref(false)
  const workspaceState = ref<WorkspaceState | null>(null)

  // Computed
  const hasWorkspaceState = computed(() => !!workspaceState.value)

  // Methods
  const initializeWorkspace = () => {
    // Load workspace state from localStorage
    const saved = localStorage.getItem('workspace_state')
    if (saved) {
      try {
        workspaceState.value = JSON.parse(saved)
      } catch (error) {
        console.error('Failed to parse workspace state:', error)
        localStorage.removeItem('workspace_state')
      }
    }
  }

  const setEngaged = (engaged: boolean) => {
    hasEngaged.value = engaged
  }

  const saveWorkspaceState = (state: Partial<WorkspaceState>) => {
    const currentState = workspaceState.value || {
      strategyType: '',
      formData: {},
      assistantHistory: [],
      lastModified: new Date()
    }

    workspaceState.value = {
      ...currentState,
      ...state,
      lastModified: new Date()
    }

    // Save to localStorage
    localStorage.setItem('workspace_state', JSON.stringify(workspaceState.value))
  }

  const clearWorkspaceState = () => {
    workspaceState.value = null
    hasEngaged.value = false
    localStorage.removeItem('workspace_state')
  }

  const restoreWorkspace = () => {
    // This would restore the full workspace state
    // Implementation depends on your specific needs
    return workspaceState.value
  }

  return {
    hasEngaged,
    workspaceState,
    hasWorkspaceState,
    initializeWorkspace,
    setEngaged,
    saveWorkspaceState,
    clearWorkspaceState,
    restoreWorkspace
  }
})