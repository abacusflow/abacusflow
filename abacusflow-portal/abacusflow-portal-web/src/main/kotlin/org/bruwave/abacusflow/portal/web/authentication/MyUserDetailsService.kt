package org.bruwave.abacusflow.portal.web.authentication

import org.bruwave.abacusflow.usecase.user.service.UserAuthenticationService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.security.core.userdetails.User as SecurityUser

@Component
class MyUserDetailsService(
    private val userAuthenticationService: UserAuthenticationService,
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        require(!username.isNullOrBlank()) { "Username must not be null or blank" }

        val user = userAuthenticationService.getUserForLogin(username) ?: throw UsernameNotFoundException("用户不存在")

        return SecurityUser.builder()
            .username(user.name)
            .password(user.password) // 应为加密后的密码
            .accountLocked(user.locked)
            .disabled(!user.enabled)
            .roles(*user.roleNames.toTypedArray())
//            .disabled(false)
//            .accountExpired(false)
//            .credentialsExpired(false)
//            .accountLocked(false)
            .build()
    }
}
