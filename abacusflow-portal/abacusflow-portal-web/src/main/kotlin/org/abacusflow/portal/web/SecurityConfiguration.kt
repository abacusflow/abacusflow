package org.abacusflow.portal.web

import org.abacusflow.portal.web.authentication.AbacusFlowAuthenticationEntryPointHandler
import org.abacusflow.portal.web.authentication.AbacusFlowFormLoginAuthFailureHandler
import org.abacusflow.portal.web.authentication.AbacusFlowFormLoginAuthSuccessHandler
import org.abacusflow.portal.web.authentication.AbacusFlowJwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    private val abacusFlowAuthenticationEntryPointHandler: AbacusFlowAuthenticationEntryPointHandler,
    private val abacusFlowFormLoginAuthSuccessHandler: AbacusFlowFormLoginAuthSuccessHandler,
    private val abacusFlowFormLoginAuthFailureHandler: AbacusFlowFormLoginAuthFailureHandler,
) {
    private val staticResources = listOf("css", "js", "images", "fonts", "scss", "vendor")

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
//        abacusFlowJwtAuthenticationFilter: AbacusFlowJwtAuthenticationFilter
    ): SecurityFilterChain {
        http {
            csrf { disable() }

            exceptionHandling {
                authenticationEntryPoint = abacusFlowAuthenticationEntryPointHandler
            }

            authorizeHttpRequests {
                authorize("/static/**", permitAll)
                authorize("/login", permitAll)
                authorize("/login", permitAll)
                authorize("/logout", permitAll)
                authorize(anyRequest, authenticated)
            }

            formLogin {
                loginPage = "/login"
                authenticationSuccessHandler = abacusFlowFormLoginAuthSuccessHandler
                authenticationFailureHandler = abacusFlowFormLoginAuthFailureHandler
                permitAll()
            }

//            oneTimeTokenLogin {  }
//            addFilterBefore<UsernamePasswordAuthenticationFilter>(abacusFlowJwtAuthenticationFilter)
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
