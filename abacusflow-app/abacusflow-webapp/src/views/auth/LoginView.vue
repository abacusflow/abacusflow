<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-header">
        <h1>小算盘</h1>
        <h3>AbacusFlow Admin</h3>
      </div>

      <div class="login-form">
        <a-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleLogin"
          block
        >
          <template #icon>
            <LoginOutlined />
          </template>
          使用Auth0登录
        </a-button>

        <div v-if="error" class="error-message">
          <a-alert :message="error" type="error" show-icon />
        </div>
      </div>

      <div class="login-footer">
        <p>© 2025 AbacusFlow. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { LoginOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref<string | null>(null)

const handleLogin = async () => {
  try {
    loading.value = true
    error.value = null

    // Get the redirect URL from query params
    const redirectTo = route.query.redirect as string || '/'

    await authStore.login(redirectTo)
  } catch (err) {
    console.error('Login failed:', err)
    error.value = '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-content {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.login-header {
  margin-bottom: 32px;
}

.login-header h1 {
  color: #1890ff;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.login-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.error-message {
  margin-top: 16px;
}

.login-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.login-footer p {
  color: #999;
  font-size: 14px;
  margin: 0;
}

@media (max-width: 480px) {
  .login-content {
    margin: 20px;
    padding: 30px 20px;
  }

  .login-header h1 {
    font-size: 28px;
  }
}
</style>