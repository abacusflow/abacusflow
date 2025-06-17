package org.bruwave.abacusflow.portal.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.ConcurrentSessionFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class SecurityConfiguration (
){
//    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                // 允许静态资源无授权访问
                authorize("/static/**", permitAll)
                // 允许登录/退出无授权访问
                authorize("/login", permitAll)
                authorize("/logout", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginPage = "/login"
                permitAll()
            }
            logout {
                // GET请求访问/logout
                logoutRequestMatcher = AntPathRequestMatcher("/logout", "GET")
                permitAll()
            }
        }
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username("asus")
            .password(passwordEncoder().encode("asus"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}