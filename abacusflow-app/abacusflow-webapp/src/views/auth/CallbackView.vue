<template>
  <div class="callback-container">
    <div class="callback-content">
      <a-spin :spinning="true" size="large">
        <div class="callback-message">
          <h2>正在处理登录...</h2>
          <p v-if="error" class="error-message">{{ error }}</p>
        </div>
      </a-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    // Handle the redirect callback
    const success = await authStore.handleRedirectCallback()

    if (success) {
      // Get the intended destination from the URL or default to dashboard
      const urlParams = new URLSearchParams(window.location.search)
      const returnTo = urlParams.get('returnTo') || '/'

      // Redirect to the intended page
      await router.replace(returnTo)
    } else {
      error.value = '登录处理失败'
      setTimeout(() => {
        router.replace('/login')
      }, 3000)
    }
  } catch (err) {
    console.error('Callback handling failed:', err)
    error.value = '登录过程中发生错误'

    setTimeout(() => {
      router.replace('/login')
    }, 3000)
  }
})
</script>

<style scoped>
.callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.callback-content {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 300px;
}

.callback-message h2 {
  margin-bottom: 16px;
  color: #1890ff;
}

.callback-message p {
  color: #666;
  margin: 8px 0;
}

.error-message {
  color: #ff4d4f !important;
}
</style>