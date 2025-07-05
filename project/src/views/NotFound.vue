<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full text-center">
      <h1 class="text-9xl font-bold text-primary-600 dark:text-primary-400">404</h1>
      <h2 class="mt-4 text-3xl font-extrabold text-gray-900 dark:text-white">Page not found</h2>
      <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">
        Sorry, we couldn't find the page you're looking for.
      </p>
      <div class="mt-6 flex justify-center space-x-4">
        <button 
          @click="goBack" 
          class="btn btn-secondary"
        >
          Go Back
        </button>
        <button 
          @click="goHome" 
          class="btn btn-primary"
        >
          Go Home
        </button>
      </div>
    </div>
  </div>
</template>