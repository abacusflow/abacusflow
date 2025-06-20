package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.portal.web.authentication.MyAuthenticationEntryPointHandler
import org.bruwave.abacusflow.portal.web.authentication.MyAuthenticationFailureHandler
import org.bruwave.abacusflow.portal.web.authentication.MyAuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration(
    private val myAuthenticationEntryPointHandler: MyAuthenticationEntryPointHandler,
    private val myAuthenticationSuccessHandler: MyAuthenticationSuccessHandler,
    private val myAuthenticationFailureHandler: MyAuthenticationFailureHandler,
) {
    private val staticResources = listOf("css", "js", "images", "fonts", "scss", "vendor")

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            exceptionHandling {
                authenticationEntryPoint = myAuthenticationEntryPointHandler
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
                authenticationSuccessHandler = myAuthenticationSuccessHandler
                authenticationFailureHandler = myAuthenticationFailureHandler
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
