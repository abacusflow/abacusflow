import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '../services/auth'

interface UserProfile {
  name?: string
  email?: string
  picture?: string
  nickname?: string
  sub?: string
  [key: string]: any
}

export const useAuthStore = defineStore('auth', () => {
  // State
  const isAuthenticated = ref(false)
  const user = ref<UserProfile | null>(null)
  const accessToken = ref<string | null>(null)
  const isLoading = ref(true)
  const error = ref<string | null>(null)

  // Getters
  const isLoggedIn = computed(() => isAuthenticated.value && !!user.value)
  const userDisplayName = computed(() => user.value?.name || user.value?.nickname || user.value?.email || 'User')

  // Actions
  const initialize = async () => {
    try {
      isLoading.value = true
      error.value = null

      await authService.initialize()

      const authenticated = await authService.isAuthenticated()
      isAuthenticated.value = authenticated

      if (authenticated) {
        await loadUserProfile()
      }
    } catch (err) {
      console.error('Failed to initialize auth:', err)
      error.value = 'Failed to initialize authentication'
    } finally {
      isLoading.value = false
    }
  }

  const login = async (redirectPath?: string) => {
    try {
      error.value = null
      await authService.login({
        appState: { returnTo: redirectPath || window.location.pathname }
      })
    } catch (err) {
      console.error('Login failed:', err)
      error.value = 'Login failed'
      throw err
    }
  }

  const logout = async () => {
    try {
      error.value = null
      await authService.logout()

      // Clear local state
      isAuthenticated.value = false
      user.value = null
      accessToken.value = null
    } catch (err) {
      console.error('Logout failed:', err)
      error.value = 'Logout failed'
      throw err
    }
  }

  const handleRedirectCallback = async () => {
    try {
      error.value = null
      await authService.handleRedirectCallback()

      const authenticated = await authService.isAuthenticated()
      isAuthenticated.value = authenticated

      if (authenticated) {
        await loadUserProfile()
      }

      return true
    } catch (err) {
      console.error('Failed to handle redirect callback:', err)
      error.value = 'Failed to handle authentication callback'
      return false
    }
  }

  const loadUserProfile = async () => {
    try {
      const [userProfile, auth0User] = await Promise.all([
        authService.getUserProfile(),
        authService.getUser()
      ])

      // Merge user data from JWT token and Auth0 user profile
      user.value = {
        ...userProfile,
        ...auth0User
      }
    } catch (err) {
      console.error('Failed to load user profile:', err)
      user.value = null
    }
  }

  const getAccessToken = async () => {
    try {
      if (!isAuthenticated.value) {
        throw new Error('User not authenticated')
      }

      const token = await authService.getAccessToken()
      accessToken.value = token
      return token
    } catch (err) {
      console.error('Failed to get access token:', err)
      throw err
    }
  }

  const refreshToken = async () => {
    try {
      const token = await authService.getAccessToken({
        cacheMode: 'off'
      })
      accessToken.value = token
      return token
    } catch (err) {
      console.error('Failed to refresh token:', err)
      // If refresh fails, redirect to login
      await login()
      throw err
    }
  }

  const checkAuthStatus = async () => {
    try {
      const authenticated = await authService.isAuthenticated()

      if (authenticated !== isAuthenticated.value) {
        isAuthenticated.value = authenticated

        if (authenticated && !user.value) {
          await loadUserProfile()
        } else if (!authenticated) {
          user.value = null
          accessToken.value = null
        }
      }

      return authenticated
    } catch (err) {
      console.error('Failed to check auth status:', err)
      return false
    }
  }

  return {
    // State
    isAuthenticated,
    user,
    accessToken,
    isLoading,
    error,

    // Getters
    isLoggedIn,
    userDisplayName,

    // Actions
    initialize,
    login,
    logout,
    handleRedirectCallback,
    loadUserProfile,
    getAccessToken,
    refreshToken,
    checkAuthStatus
  }
})