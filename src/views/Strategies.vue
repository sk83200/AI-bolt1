<template>
  <div class="h-full">
    <!-- Strategies Page Layout with Resizable Split Panes -->
    <Splitpanes class="h-full">
      <!-- Left: Split pane with GUI Editor Form (top) and Messages/Notifications (bottom) -->
      <Pane :size="60" min-size="40">
        <Splitpanes horizontal class="h-full">
          <!-- Top: GUI Editor Form -->
          <Pane :size="70" min-size="40">
            <div class="h-full bg-white dark:bg-gray-800">
              <StrategyGuiEditor 
                v-model="strategyData"
                :is-guest="authStore.guestMode || !authStore.isAuthenticated"
                @update="handleFormUpdate"
              />
            </div>
          </Pane>
          
          <!-- Bottom: Messages/Notifications Panel -->
          <Pane :size="30" min-size="20">
            <div class="h-full bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700">
              <StrategyNotifications 
                :notifications="notifications"
                :is-guest="authStore.guestMode || !authStore.isAuthenticated"
              />
            </div>
          </Pane>
        </Splitpanes>
      </Pane>
      
      <!-- Right: Output/Code Panel -->
      <Pane :size="40" min-size="30">
        <StrategyOutputPanel 
          :strategy-data="strategyData"
          :is-guest="authStore.guestMode || !authStore.isAuthenticated"
          :generated-code="generatedCode"
        />
      </Pane>
    </Splitpanes>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { useAuthStore } from '../stores/auth'
import StrategyGuiEditor from '../components/strategies/StrategyGuiEditor.vue'
import StrategyNotifications from '../components/strategies/StrategyNotifications.vue'
import StrategyOutputPanel from '../components/strategies/StrategyOutputPanel.vue'

const authStore = useAuthStore()

// Strategy data - with sample data for guests
const strategyData = reactive({
  name: (authStore.guestMode || !authStore.isAuthenticated) ? 'Sample Momentum Strategy' : '',
  type: 'momentum',
  assetClass: 'stocks',
  timeframe: '1d',
  entryRules: {
    conditions: (authStore.guestMode || !authStore.isAuthenticated) ? [
      { text: 'RSI > 70' },
      { text: 'Price > SMA20' },
      { text: 'Volume > 1.5x average' }
    ] : [{ text: '' }],
    indicators: (authStore.guestMode || !authStore.isAuthenticated) ? ['RSI', 'SMA', 'Volume'] : []
  },
  exitRules: {
    profitTarget: 5,
    stopLoss: 2,
    useTrailingStop: false
  },
  riskManagement: {
    maxRiskPerTrade: 2,
    maxPositions: 5
  }
})

// Generated code from form changes
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
    message: (authStore.guestMode || !authStore.isAuthenticated) 
      ? 'Guest mode: Save and backtest features are disabled' 
      : 'Ready to build strategies',
    timestamp: new Date()
  }
])

// Handle form updates
const handleFormUpdate = (formData: any) => {
  // Only allow updates for authenticated users
  if (!authStore.guestMode && authStore.isAuthenticated) {
    Object.assign(strategyData, formData)
    generateCode()
    
    // Add notification
    notifications.value.unshift({
      id: Date.now(),
      type: 'info',
      message: 'Strategy parameters updated',
      timestamp: new Date()
    })
  }
}

// Generate code based on strategy data
const generateCode = () => {
  // Only generate code for authenticated users
  if (authStore.guestMode || !authStore.isAuthenticated) {
    generatedCode.value = ''
    return
  }

  // Mock code generation for authenticated users
  generatedCode.value = `# Generated Trading Strategy: ${strategyData.name || 'Untitled Strategy'}

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
        ${strategyData.entryRules.indicators?.map(ind => `self.${ind.toLowerCase()} = Indicator.${ind}(period=14)`).join('\n        ') || '# No indicators selected'}
        
    def entry_conditions(self, data):
        # Entry logic based on ${strategyData.type} strategy
        conditions = [
            ${strategyData.entryRules.conditions?.map(c => `# ${c.text || 'Empty condition'}`).join('\n            ') || '# No entry conditions defined'}
        ]
        return all(conditions)
        
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

// Initialize code generation for authenticated users
if (!authStore.guestMode && authStore.isAuthenticated) {
  generateCode()
}
</script>

<style>
/* Custom splitpanes styling */
.splitpanes__splitter {
  @apply bg-gray-300 dark:bg-gray-600 hover:bg-gray-400 dark:hover:bg-gray-500 transition-colors;
}

.splitpanes--vertical > .splitpanes__splitter {
  width: 4px !important;
  margin: 0 -2px;
}

.splitpanes--horizontal > .splitpanes__splitter {
  height: 4px !important;
  margin: -2px 0;
}

.splitpanes__splitter:hover {
  @apply bg-primary-400 dark:bg-primary-600;
}
</style>