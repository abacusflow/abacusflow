package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.portal.web.authentication.AbacusFlowAuthenticationEntryPointHandler
import org.bruwave.abacusflow.portal.web.authentication.AbacusFlowAuthenticationFailureHandler
import org.bruwave.abacusflow.portal.web.authentication.AbacusFlowAuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration(
    private val abacusFlowAuthenticationEntryPointHandler: AbacusFlowAuthenticationEntryPointHandler,
    private val abacusFlowAuthenticationSuccessHandler: AbacusFlowAuthenticationSuccessHandler,
    private val abacusFlowAuthenticationFailureHandler: AbacusFlowAuthenticationFailureHandler,
) {
    private val staticResources = listOf("css", "js", "images", "fonts", "scss", "vendor")

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            exceptionHandling {
                authenticationEntryPoint = abacusFlowAuthenticationEntryPointHandler
            }

            authorizeHttpRequests {
//                staticResources.forEach {
//                    authorize("/$it/**", permitAll)
//                }
                authorize("/static/**", permitAll)
                authorize("/login", permitAll)
                authorize("/login", permitAll)
                authorize("/logout", permitAll)
                authorize(anyRequest, authenticated)
            }

            formLogin {
                loginPage = "/login"
                authenticationSuccessHandler = abacusFlowAuthenticationSuccessHandler
                authenticationFailureHandler = abacusFlowAuthenticationFailureHandler
                permitAll()
            }

//            //TODO ADDD BASIC -> NO AUTH
//            httpBasic {
//            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
