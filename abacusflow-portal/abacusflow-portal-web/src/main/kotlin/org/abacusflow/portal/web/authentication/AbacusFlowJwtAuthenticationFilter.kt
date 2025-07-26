package org.abacusflow.portal.web.authentication

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.web.filter.OncePerRequestFilter

class AbacusFlowJwtAuthenticationFilter(
    private val authenticationConfiguration: AuthenticationConfiguration,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        TODO("Not yet implemented")
    }

}