<template>
  <div class="h-full">
    <!-- Strategies Page Layout -->
    <div class="h-full flex">
      <!-- Left: AI Assistant Panel -->
      <div class="w-1/3 border-r border-gray-200 dark:border-gray-700">
        <StrategiesAiAssistant 
          @strategy-update="handleStrategyUpdate"
          :is-guest="authStore.guestMode"
        />
      </div>
      
      <!-- Right: Split Pane with GUI Editor and Output -->
      <div class="flex-1 flex">
        <!-- Middle: Split pane with GUI Editor Form (top) and Messages/Notifications (bottom) -->
        <div class="w-1/2 border-r border-gray-200 dark:border-gray-700">
          <Splitpanes horizontal class="h-full">
            <!-- Top: GUI Editor Form -->
            <Pane :size="70" min-size="40">
              <div class="h-full bg-white dark:bg-gray-800">
                <StrategyGuiEditor 
                  v-model="strategyData"
                  :is-guest="authStore.guestMode"
                  @update="handleFormUpdate"
                />
              </div>
            </Pane>
            
            <!-- Bottom: Messages/Notifications Panel -->
            <Pane :size="30" min-size="20">
              <div class="h-full bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700">
                <StrategyNotifications 
                  :notifications="notifications"
                  :is-guest="authStore.guestMode"
                />
              </div>
            </Pane>
          </Splitpanes>
        </div>
        
        <!-- Right: Output/Code Panel -->
        <div class="w-1/2">
          <StrategyOutputPanel 
            :strategy-data="strategyData"
            :is-guest="authStore.guestMode"
            :generated-code="generatedCode"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { useAuthStore } from '../stores/auth'
import StrategiesAiAssistant from '../components/strategies/StrategiesAiAssistant.vue'
import StrategyGuiEditor from '../components/strategies/StrategyGuiEditor.vue'
import StrategyNotifications from '../components/strategies/StrategyNotifications.vue'
import StrategyOutputPanel from '../components/strategies/StrategyOutputPanel.vue'

const authStore = useAuthStore()

// Strategy data
const strategyData = reactive({
  name: '',
  type: 'momentum',
  assetClass: 'stocks',
  timeframe: '1d',
  entryRules: {
    conditions: [],
    indicators: []
  },
  exitRules: {
    profitTarget: 5,
    stopLoss: 2
  },
  riskManagement: {
    maxRiskPerTrade: 2,
    maxPositions: 5
  }
})

// Generated code from AI
const generatedCode = ref('')

// Notifications
const notifications = ref([
  {
    id: 1,
    type: 'info',
    message: 'Strategy builder loaded successfully',
    timestamp: new Date()
  },
  {
    id: 2,
    type: 'warning',
    message: authStore.guestMode ? 'Guest mode: Limited functionality available' : 'Ready to build strategies',
    timestamp: new Date()
  }
])

// Handle strategy updates from AI Assistant
const handleStrategyUpdate = (update: any) => {
  Object.assign(strategyData, update)
  
  // Add notification
  notifications.value.unshift({
    id: Date.now(),
    type: 'success',
    message: 'Strategy updated from AI suggestions',
    timestamp: new Date()
  })
}

// Handle form updates
const handleFormUpdate = (formData: any) => {
  Object.assign(strategyData, formData)
  
  // Generate code if not in guest mode
  if (!authStore.guestMode) {
    generateCode()
  }
}

// Generate code based on strategy data
const generateCode = () => {
  // Mock code generation
  generatedCode.value = `
# Generated Trading Strategy: ${strategyData.name || 'Untitled Strategy'}

import pandas as pd
import numpy as np
from trading_framework import Strategy, Indicator

class ${strategyData.name?.replace(/\s+/g, '') || 'CustomStrategy'}(Strategy):
    def __init__(self):
        super().__init__()
        self.asset_class = "${strategyData.assetClass}"
        self.timeframe = "${strategyData.timeframe}"
        
    def setup_indicators(self):
        # Setup technical indicators
        self.rsi = Indicator.RSI(period=14)
        self.sma_20 = Indicator.SMA(period=20)
        self.sma_50 = Indicator.SMA(period=50)
        
    def entry_conditions(self, data):
        # Entry logic based on ${strategyData.type} strategy
        return (
            data['rsi'] > 50 and
            data['sma_20'] > data['sma_50'] and
            data['volume'] > data['avg_volume']
        )
        
    def exit_conditions(self, data, position):
        # Exit logic
        profit_target = ${strategyData.exitRules.profitTarget}
        stop_loss = ${strategyData.exitRules.stopLoss}
        
        return (
            position.unrealized_pnl_pct >= profit_target or
            position.unrealized_pnl_pct <= -stop_loss
        )
        
    def position_size(self, data):
        # Risk management
        max_risk = ${strategyData.riskManagement.maxRiskPerTrade}
        return self.calculate_position_size(max_risk)
`
}
</script>