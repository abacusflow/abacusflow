package org.abacusflow.portal.web.authentication

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

/**
 * 自定义异常处理器，目的是为了将登录失败用户名作为参数传递到登录页面,当用户登录失败后能回显用户名
 */
@Component
class AbacusFlowAuthenticationFailureHandler : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        println("===== AUTHENTICATION FAILURE =====")
        println("异常类型: ${exception.javaClass.name}")
        println("错误信息: ${exception.message}")

        // 将登录失败的异常信息保存到会话中
        request.session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception)
        response.sendRedirect("/login?error")
    }
}
