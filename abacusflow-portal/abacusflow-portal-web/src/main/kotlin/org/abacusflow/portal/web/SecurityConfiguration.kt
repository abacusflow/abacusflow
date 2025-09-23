package org.abacusflow.portal.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {
    private val staticResources = listOf("css", "js", "images", "fonts", "scss", "vendor")

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            authorizeHttpRequests {
                authorize("/static/**", permitAll)
                authorize("/login", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }

            oauth2Login {
                loginPage = "/oauth2/authorization/oauth2-client"
                defaultSuccessUrl("/", true)
                failureUrl("/login?error")
            }

            logout {
                logoutSuccessUrl = "/login?logout"
                deleteCookies("JSESSIONID")
                permitAll()
            }

            sessionManagement {
                maximumSessions = 1
                maxSessionsPreventsLogin = false
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
