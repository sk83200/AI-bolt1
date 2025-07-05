<template>
  <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 h-[calc(100vh-200px)]">
    <!-- Property Sheet Panel (Left Side) -->
    <div class="lg:col-span-1 bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
      <div class="h-full flex flex-col">
        <!-- Property Sheet Header -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900">
          <h3 class="text-sm font-medium text-gray-900 dark:text-white">Strategy Properties</h3>
        </div>
        
        <!-- Property Sheet Content -->
        <div class="flex-1 overflow-y-auto">
          <!-- Basic Information Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('basic')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Basic Information</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.basic ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.basic" class="px-4 pb-4 space-y-3">
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Name</label>
                <input
                  v-model="strategyData.name"
                  type="text"
                  class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white"
                  placeholder="Strategy Name"
                />
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Type</label>
                <select v-model="strategyData.type" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white" @change="onStrategyTypeChange">
                  <option value="momentum">Momentum</option>
                  <option value="mean_reversion">Mean Reversion</option>
                  <option value="trend_following">Trend Following</option>
                  <option value="breakout">Breakout</option>
                  <option value="arbitrage">Arbitrage</option>
                  <option value="custom">Custom</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Asset Class</label>
                <select v-model="strategyData.assetClass" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
                  <option value="stocks">Stocks</option>
                  <option value="crypto">Cryptocurrency</option>
                  <option value="forex">Forex</option>
                  <option value="commodities">Commodities</option>
                  <option value="bonds">Bonds</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Timeframe</label>
                <select v-model="strategyData.timeframe" class="w-full px-2 py-1 text-xs border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-1 focus:ring-primary-500 dark:bg-gray-700 dark:text-white">
                  <option value="1m">1 Minute</option>
                  <option value="5m">5 Minutes</option>
                  <option value="15m">15 Minutes</option>
                  <option value="1h">1 Hour</option>
                  <option value="4h">4 Hours</option>
                  <option value="1d">1 Day</option>
                  <option value="1w">1 Week</option>
                </select>
              </div>
            </div>
          </div>

          <!-- Core Logic Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('coreLogic')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Core Logic</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.coreLogic ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.coreLogic" class="px-4 pb-4">
              <PropertyCoreLogicForm 
                v-model="strategyData.coreLogic" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>

          <!-- Entry Rules Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('entryRules')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Entry Rules</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.entryRules ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.entryRules" class="px-4 pb-4">
              <PropertyEntryRulesForm 
                v-model="strategyData.entryRules" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>

          <!-- Exit Rules Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('exitRules')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Exit Rules</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.exitRules ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.exitRules" class="px-4 pb-4">
              <PropertyExitRulesForm 
                v-model="strategyData.exitRules" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>

          <!-- Risk Management Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('riskManagement')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Risk Management</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.riskManagement ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.riskManagement" class="px-4 pb-4">
              <PropertyRiskManagementForm 
                v-model="strategyData.riskManagement" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>

          <!-- Position Sizing Section -->
          <div class="border-b border-gray-200 dark:border-gray-700">
            <button
              @click="togglePropertySection('positionSizing')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Position Sizing</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.positionSizing ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.positionSizing" class="px-4 pb-4">
              <PropertyPositionSizingForm 
                v-model="strategyData.positionSizing" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>

          <!-- Advanced Settings Section -->
          <div>
            <button
              @click="togglePropertySection('advanced')"
              class="w-full px-4 py-3 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700"
            >
              <span class="text-sm font-medium text-gray-900 dark:text-white">Advanced</span>
              <ChevronDownIcon 
                :class="['h-4 w-4 transition-transform', openPropertySections.advanced ? 'rotate-180' : '']"
              />
            </button>
            <div v-show="openPropertySections.advanced" class="px-4 pb-4">
              <PropertyAdvancedSettingsForm 
                v-model="strategyData.advanced" 
                :strategy-type="strategyData.type"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content Area (Right Side) -->
    <div class="lg:col-span-3 space-y-6">
      <!-- AI Prompt Section -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">AI Strategy Generator</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Describe your trading strategy in natural language:
            </label>
            <textarea
              v-model="aiPrompt"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:text-white"
              placeholder="e.g., 'Create a momentum strategy that buys when RSI is above 70 and price breaks above 20-day moving average, with 2% stop loss and 1:3 risk-reward ratio'"
            ></textarea>
          </div>
          <div class="flex justify-between items-center">
            <div class="text-sm text-gray-500 dark:text-gray-400">
              The AI will automatically populate the property sheet based on your description.
            </div>
            <button 
              @click="processAIPrompt"
              class="btn btn-primary"
              :disabled="isProcessing || !aiPrompt.trim()"
            >
              {{ isProcessing ? 'Processing...' : 'Generate Strategy' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Strategy Visualization -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">Strategy Overview</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="text-sm text-gray-500 dark:text-gray-400">Strategy Type</div>
            <div class="text-lg font-semibold text-gray-900 dark:text-white capitalize">
              {{ strategyData.type.replace('_', ' ') }}
            </div>
          </div>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="text-sm text-gray-500 dark:text-gray-400">Asset Class</div>
            <div class="text-lg font-semibold text-gray-900 dark:text-white capitalize">
              {{ strategyData.assetClass }}
            </div>
          </div>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="text-sm text-gray-500 dark:text-gray-400">Timeframe</div>
            <div class="text-lg font-semibold text-gray-900 dark:text-white">
              {{ strategyData.timeframe }}
            </div>
          </div>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="text-sm text-gray-500 dark:text-gray-400">Risk Level</div>
            <div class="text-lg font-semibold text-gray-900 dark:text-white">
              {{ getRiskLevel() }}
            </div>
          </div>
        </div>

        <!-- Strategy Description -->
        <div v-if="strategyData.description" class="mb-6">
          <h4 class="text-sm font-medium text-gray-900 dark:text-white mb-2">Description</h4>
          <p class="text-sm text-gray-600 dark:text-gray-300 bg-gray-50 dark:bg-gray-700 rounded-lg p-3">
            {{ strategyData.description }}
          </p>
        </div>

        <!-- Action Buttons -->
        <div class="flex space-x-3">
          <button 
            @click="saveStrategy"
            class="btn btn-primary"
            :disabled="!strategyData.name"
          >
            Save Strategy
          </button>
          <button 
            @click="runBacktest"
            class="btn btn-secondary"
            :disabled="!strategyData.name"
          >
            Run Backtest
          </button>
          <button 
            @click="resetStrategy"
            class="btn btn-secondary"
          >
            Reset
          </button>
          <button 
            @click="exportStrategy"
            class="btn btn-secondary"
            :disabled="!strategyData.name"
          >
            Export JSON
          </button>
        </div>
      </div>

      <!-- JSON Preview (Collapsible) -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md">
        <button
          @click="showJsonPreview = !showJsonPreview"
          class="w-full px-6 py-4 text-left flex items-center justify-between hover:bg-gray-50 dark:hover:bg-gray-700 rounded-t-lg"
        >
          <h3 class="text-lg font-medium text-gray-900 dark:text-white">Strategy JSON</h3>
          <ChevronDownIcon 
            :class="['h-5 w-5 transition-transform', showJsonPreview ? 'rotate-180' : '']"
          />
        </button>
        <div v-show="showJsonPreview" class="px-6 pb-6">
          <div class="bg-gray-50 dark:bg-gray-900 rounded-lg p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Strategy Configuration</span>
              <button 
                @click="copyToClipboard"
                class="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400"
              >
                Copy
              </button>
            </div>
            <pre class="text-xs text-gray-600 dark:text-gray-300 overflow-auto max-h-60 whitespace-pre-wrap">{{ JSON.stringify(strategyData, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ChevronDownIcon } from '@heroicons/vue/24/outline'
import PropertyCoreLogicForm from './strategy-forms/PropertyCoreLogicForm.vue'
import PropertyEntryRulesForm from './strategy-forms/PropertyEntryRulesForm.vue'
import PropertyExitRulesForm from './strategy-forms/PropertyExitRulesForm.vue'
import PropertyRiskManagementForm from './strategy-forms/PropertyRiskManagementForm.vue'
import PropertyPositionSizingForm from './strategy-forms/PropertyPositionSizingForm.vue'
import PropertyAdvancedSettingsForm from './strategy-forms/PropertyAdvancedSettingsForm.vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

// AI Prompt processing
const aiPrompt = ref('')
const isProcessing = ref(false)
const showJsonPreview = ref(false)

// Property sheet panel states
const openPropertySections = reactive({
  basic: true,
  coreLogic: false,
  entryRules: false,
  exitRules: false,
  riskManagement: false,
  positionSizing: false,
  advanced: false
})

// Strategy data structure
const strategyData = reactive({
  name: '',
  type: 'momentum',
  assetClass: 'stocks',
  timeframe: '1d',
  description: '',
  coreLogic: {},
  entryRules: {},
  exitRules: {},
  riskManagement: {},
  positionSizing: {},
  advanced: {}
})

// Toggle property section visibility
const togglePropertySection = (section: string) => {
  openPropertySections[section] = !openPropertySections[section]
}

// Get risk level based on strategy configuration
const getRiskLevel = () => {
  const riskPerTrade = strategyData.riskManagement?.maxRiskPerTrade || 2
  if (riskPerTrade <= 1) return 'Low'
  if (riskPerTrade <= 3) return 'Medium'
  return 'High'
}

// Initialize default values based on strategy type
const initializeStrategyDefaults = (type: string) => {
  const defaults = {
    momentum: {
      coreLogic: {
        indicators: ['RSI', 'MACD'],
        rsiThreshold: 70,
        lookbackPeriod: 14
      },
      entryRules: {
        conditions: [{ text: 'RSI > 70', operator: 'AND' }],
        confirmationBars: 1
      },
      exitRules: {
        profitTarget: 5,
        stopLoss: 2,
        useTrailingStop: false
      },
      riskManagement: {
        maxRiskPerTrade: 2,
        maxPositions: 5
      },
      positionSizing: {
        method: 'risk_based',
        riskPerTrade: 2
      }
    },
    mean_reversion: {
      coreLogic: {
        indicators: ['RSI', 'BB'],
        oversoldLevel: 30,
        overboughtLevel: 70
      },
      entryRules: {
        conditions: [{ text: 'RSI < 30', operator: 'AND' }],
        confirmationBars: 2
      },
      exitRules: {
        profitTarget: 3,
        stopLoss: 1.5,
        useTrailingStop: false
      },
      riskManagement: {
        maxRiskPerTrade: 1.5,
        maxPositions: 3
      },
      positionSizing: {
        method: 'volatility_adjusted',
        atrMultiplier: 2
      }
    }
  }
  
  return defaults[type] || defaults.momentum
}

// Handle strategy type change
const onStrategyTypeChange = () => {
  const defaults = initializeStrategyDefaults(strategyData.type)
  Object.assign(strategyData.coreLogic, defaults.coreLogic)
  Object.assign(strategyData.entryRules, defaults.entryRules)
  Object.assign(strategyData.exitRules, defaults.exitRules)
  Object.assign(strategyData.riskManagement, defaults.riskManagement)
  Object.assign(strategyData.positionSizing, defaults.positionSizing)
}

// Process AI prompt
const processAIPrompt = async () => {
  if (!aiPrompt.value.trim()) return
  
  isProcessing.value = true
  try {
    // Simulate AI processing
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // Mock AI response
    const aiResponse = parseAIPrompt(aiPrompt.value)
    
    // Update strategy data with AI response
    Object.assign(strategyData, aiResponse)
    
    // Open relevant property sections
    openPropertySections.coreLogic = true
    openPropertySections.entryRules = true
    openPropertySections.exitRules = true
    if (aiResponse.riskManagement && Object.keys(aiResponse.riskManagement).length > 0) {
      openPropertySections.riskManagement = true
    }
    
  } catch (error) {
    console.error('Error processing AI prompt:', error)
  } finally {
    isProcessing.value = false
  }
}

// Mock AI prompt parser
const parseAIPrompt = (prompt: string) => {
  const lowerPrompt = prompt.toLowerCase()
  
  let strategyType = 'momentum'
  if (lowerPrompt.includes('mean reversion') || lowerPrompt.includes('oversold') || lowerPrompt.includes('overbought')) {
    strategyType = 'mean_reversion'
  } else if (lowerPrompt.includes('trend') || lowerPrompt.includes('moving average')) {
    strategyType = 'trend_following'
  } else if (lowerPrompt.includes('breakout') || lowerPrompt.includes('break')) {
    strategyType = 'breakout'
  }
  
  const result = {
    name: extractStrategyName(prompt) || 'AI Generated Strategy',
    type: strategyType,
    description: `Strategy generated from: "${prompt}"`,
    coreLogic: extractCoreLogic(prompt, strategyType),
    entryRules: extractEntryRules(prompt),
    exitRules: extractExitRules(prompt),
    riskManagement: extractRiskManagement(prompt),
    positionSizing: extractPositionSizing(prompt)
  }
  
  return result
}

// Helper functions for AI parsing
const extractStrategyName = (prompt: string) => {
  const words = prompt.split(' ').slice(0, 4)
  return words.join(' ').replace(/[^a-zA-Z0-9\s]/g, '') + ' Strategy'
}

const extractCoreLogic = (prompt: string, type: string) => {
  const logic = {}
  if (prompt.includes('RSI')) logic.indicators = ['RSI']
  if (prompt.includes('MACD')) logic.indicators = [...(logic.indicators || []), 'MACD']
  if (prompt.includes('moving average')) logic.indicators = [...(logic.indicators || []), 'SMA']
  
  const rsiMatch = prompt.match(/RSI.*?(\d+)/i)
  if (rsiMatch) logic.rsiThreshold = parseInt(rsiMatch[1])
  
  return logic
}

const extractEntryRules = (prompt: string) => {
  const rules = { conditions: [] }
  
  if (prompt.includes('RSI')) {
    const rsiMatch = prompt.match(/RSI.*?(above|below|>|<).*?(\d+)/i)
    if (rsiMatch) {
      const operator = rsiMatch[1].includes('above') || rsiMatch[1] === '>' ? '>' : '<'
      rules.conditions.push({ text: `RSI ${operator} ${rsiMatch[2]}`, operator: 'AND' })
    }
  }
  
  if (prompt.includes('moving average') || prompt.includes('MA')) {
    rules.conditions.push({ text: 'Price > MA20', operator: 'AND' })
  }
  
  return rules
}

const extractExitRules = (prompt: string) => {
  const rules = {}
  
  const stopLossMatch = prompt.match(/(\d+(?:\.\d+)?)%?\s*stop\s*loss/i)
  if (stopLossMatch) rules.stopLoss = parseFloat(stopLossMatch[1])
  
  const profitMatch = prompt.match(/(\d+(?:\.\d+)?)%?\s*profit/i)
  if (profitMatch) rules.profitTarget = parseFloat(profitMatch[1])
  
  const riskRewardMatch = prompt.match(/1:(\d+)\s*risk.reward/i)
  if (riskRewardMatch && rules.stopLoss) {
    rules.profitTarget = rules.stopLoss * parseInt(riskRewardMatch[1])
  }
  
  return rules
}

const extractRiskManagement = (prompt: string) => {
  const risk = {}
  
  const maxRiskMatch = prompt.match(/(\d+(?:\.\d+)?)%?\s*risk/i)
  if (maxRiskMatch) risk.maxRiskPerTrade = parseFloat(maxRiskMatch[1])
  
  return risk
}

const extractPositionSizing = (prompt: string) => {
  const sizing = {}
  
  const percentMatch = prompt.match(/(\d+(?:\.\d+)?)%\s*position/i)
  if (percentMatch) {
    sizing.method = 'fixed_percentage'
    sizing.portfolioPercentage = parseFloat(percentMatch[1])
  }
  
  return sizing
}

// Strategy actions
const saveStrategy = async () => {
  try {
    console.log('Saving strategy:', strategyData)
    alert('Strategy saved successfully!')
  } catch (error) {
    console.error('Error saving strategy:', error)
    alert('Failed to save strategy')
  }
}

const runBacktest = async () => {
  try {
    console.log('Running backtest for strategy:', strategyData)
    alert('Backtest started! Results will be available shortly.')
  } catch (error) {
    console.error('Error running backtest:', error)
    alert('Failed to start backtest')
  }
}

const resetStrategy = () => {
  Object.assign(strategyData, {
    name: '',
    type: 'momentum',
    assetClass: 'stocks',
    timeframe: '1d',
    description: '',
    coreLogic: {},
    entryRules: {},
    exitRules: {},
    riskManagement: {},
    positionSizing: {},
    advanced: {}
  })
  aiPrompt.value = ''
  onStrategyTypeChange()
}

const exportStrategy = async () => {
  try {
    const dataStr = JSON.stringify(strategyData, null, 2)
    const dataBlob = new Blob([dataStr], { type: 'application/json' })
    const url = URL.createObjectURL(dataBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${strategyData.name || 'strategy'}.json`
    link.click()
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error exporting strategy:', error)
    alert('Failed to export strategy')
  }
}

const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(JSON.stringify(strategyData, null, 2))
    alert('Strategy JSON copied to clipboard!')
  } catch (error) {
    console.error('Failed to copy to clipboard:', error)
  }
}

// Initialize with default values
onStrategyTypeChange()
</script>