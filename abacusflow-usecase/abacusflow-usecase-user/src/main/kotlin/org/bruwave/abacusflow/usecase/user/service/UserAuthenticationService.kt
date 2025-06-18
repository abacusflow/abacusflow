package org.bruwave.abacusflow.usecase.user.service

import org.bruwave.abacusflow.usecase.user.UserDetailsForLoginTO

interface UserAuthenticationService {
    fun getUserForLogin(username: String): UserDetailsForLoginTO?
    fun validateCredentials(username: String, password: String): Boolean
    fun updateLastLoginTime(userId: Long)
}