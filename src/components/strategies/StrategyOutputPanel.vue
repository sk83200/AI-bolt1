<template>
  <div class="h-full bg-white dark:bg-gray-800 flex flex-col">
    <!-- Header -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
      <div class="flex items-center justify-between">
        <h3 class="font-medium text-gray-900 dark:text-white">Strategy Output</h3>
        <div class="flex items-center space-x-2">
          <!-- Language tabs for all users -->
          <div class="flex rounded-md shadow-sm">
            <button
              @click="activeTab = 'json'"
              :class="[
                'px-3 py-1 text-xs font-medium rounded-l-md border',
                activeTab === 'json'
                  ? 'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800'
                  : 'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'
              ]"
            >
              JSON
            </button>
            <button
              @click="activeTab = 'python'"
              :class="[
                'px-3 py-1 text-xs font-medium border-t border-b',
                activeTab === 'python'
                  ? 'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800'
                  : 'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'
              ]"
            >
              Python
            </button>
            <button
              @click="activeTab = 'java'"
              :class="[
                'px-3 py-1 text-xs font-medium rounded-r-md border',
                activeTab === 'java'
                  ? 'bg-primary-100 text-primary-800 border-primary-300 dark:bg-primary-900 dark:text-primary-200 dark:border-primary-800'
                  : 'bg-white text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600'
              ]"
            >
              Java
            </button>
          </div>
          <button 
            v-if="!isGuest && getCurrentCode()"
            @click="copyCode"
            class="text-xs text-primary-600 hover:text-primary-700 dark:text-primary-400"
          >
            Copy
          </button>
        </div>
      </div>
    </div>
    
    <!-- Content -->
    <div class="flex-1 overflow-hidden">
      <div class="h-full overflow-auto p-4">
        <!-- Code display (read-only for guests) -->
        <div class="bg-gray-900 rounded-lg p-4 text-green-400 font-mono text-xs overflow-auto h-full">
          <pre>{{ getCurrentCode() }}</pre>
        </div>
        
        <!-- Guest mode overlay -->
        <div v-if="isGuest" class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div class="bg-white dark:bg-gray-800 rounded-lg p-6 text-center max-w-sm mx-4">
            <div class="text-4xl mb-4">🔒</div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">
              Code Editing Restricted
            </h4>
            <p class="text-sm text-gray-600 dark:text-gray-300 mb-4">
              This code cannot be edited in guest mode. Sign up to customize and export your strategies.
            </p>
            <div class="space-y-2">
              <button 
                @click="$router.push('/register')"
                class="w-full bg-primary-600 hover:bg-primary-700 text-white text-sm px-4 py-2 rounded-md font-medium"
              >
                Sign Up for Full Access
              </button>
              <button 
                @click="$router.push('/login')"
                class="w-full text-primary-600 hover:text-primary-700 dark:text-primary-400 text-sm px-4 py-2"
              >
                Log In
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// Props
const props = defineProps<{
  strategyData: any
  isGuest: boolean
  generatedCode: string
}>()

const router = useRouter()
const activeTab = ref('json')

// Sample codes for different languages
const sampleCodes = {
  json: computed(() => `{
  "strategy": {
    "name": "Sample Momentum Strategy",
    "type": "momentum",
    "assetClass": "stocks",
    "timeframe": "1d",
    "entryRules": {
      "conditions": [
        "RSI > 70",
        "Price > SMA20",
        "Volume > 1.5x average"
      ],
      "indicators": ["RSI", "SMA", "Volume"]
    },
    "exitRules": {
      "profitTarget": 5,
      "stopLoss": 2,
      "useTrailingStop": false
    },
    "riskManagement": {
      "maxRiskPerTrade": 2,
      "maxPositions": 5
    }
  },
  "backtest": {
    "startDate": "2023-01-01",
    "endDate": "2023-12-31",
    "initialCapital": 100000,
    "commission": 0.001
  }
}`),

  python: computed(() => `# Sample Trading Strategy - Python Implementation
import pandas as pd
import numpy as np
from trading_framework import Strategy, Indicator

class SampleMomentumStrategy(Strategy):
    def __init__(self):
        super().__init__()
        self.asset_class = "stocks"
        self.timeframe = "1d"
        
    def setup_indicators(self):
        # Technical indicators
        self.rsi = Indicator.RSI(period=14)
        self.sma_20 = Indicator.SMA(period=20)
        self.volume_avg = Indicator.SMA(period=20, field='volume')
        
    def entry_conditions(self, data):
        # Sample entry logic
        return (
            data['rsi'] > 70 and
            data['close'] > data['sma_20'] and
            data['volume'] > data['volume_avg'] * 1.5
        )
        
    def exit_conditions(self, data, position):
        # Sample exit logic
        profit_target = 5.0  # 5%
        stop_loss = 2.0      # 2%
        
        return (
            position.unrealized_pnl_pct >= profit_target or
            position.unrealized_pnl_pct <= -stop_loss
        )
        
    def position_size(self, data):
        # Risk management
        max_risk = 2.0  # 2% risk per trade
        return self.calculate_position_size(max_risk)

# Sign up to generate custom strategies!`),

  java: computed(() => `// Sample Trading Strategy - Java Implementation
package com.trading.strategies;

import com.trading.framework.Strategy;
import com.trading.framework.Indicator;
import com.trading.framework.MarketData;
import com.trading.framework.Position;

public class SampleMomentumStrategy extends Strategy {
    
    private Indicator rsi;
    private Indicator sma20;
    private Indicator volumeAvg;
    
    public SampleMomentumStrategy() {
        super();
        this.assetClass = "stocks";
        this.timeframe = "1d";
        setupIndicators();
    }
    
    private void setupIndicators() {
        this.rsi = new Indicator.RSI(14);
        this.sma20 = new Indicator.SMA(20);
        this.volumeAvg = new Indicator.SMA(20, "volume");
    }
    
    @Override
    public boolean entryConditions(MarketData data) {
        return data.getRsi() > 70 &&
               data.getClose() > data.getSma20() &&
               data.getVolume() > data.getVolumeAvg() * 1.5;
    }
    
    @Override
    public boolean exitConditions(MarketData data, Position position) {
        double profitTarget = 5.0; // 5%
        double stopLoss = 2.0;     // 2%
        
        return position.getUnrealizedPnlPct() >= profitTarget ||
               position.getUnrealizedPnlPct() <= -stopLoss;
    }
    
    @Override
    public double positionSize(MarketData data) {
        double maxRisk = 2.0; // 2% risk per trade
        return calculatePositionSize(maxRisk);
    }
}

// Sign up to generate custom strategies!`)
}

// Get current code based on active tab
const getCurrentCode = () => {
  if (props.isGuest) {
    return sampleCodes[activeTab.value].value
  } else {
    // For authenticated users, show generated code or sample
    if (activeTab.value === 'python' && props.generatedCode) {
      return props.generatedCode
    } else if (activeTab.value === 'json') {
      return JSON.stringify(props.strategyData, null, 2)
    } else {
      return sampleCodes[activeTab.value].value
    }
  }
}

// Copy code to clipboard
const copyCode = async () => {
  if (props.isGuest) return
  
  try {
    await navigator.clipboard.writeText(getCurrentCode())
    // You could add a toast notification here
    console.log('Code copied to clipboard')
  } catch (error) {
    console.error('Failed to copy code:', error)
  }
}
</script>