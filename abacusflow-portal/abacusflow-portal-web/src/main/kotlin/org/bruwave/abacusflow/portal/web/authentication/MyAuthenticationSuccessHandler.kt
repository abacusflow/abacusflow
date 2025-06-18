package org.bruwave.abacusflow.portal.web.authentication

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bruwave.abacusflow.usecase.user.service.UserAuthenticationService
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

@Component
class MyAuthenticationSuccessHandler(
    private val userAuthenticationService: UserAuthenticationService,
) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        /**
         * 重定向到登录页
         */
        val redirect = request.getParameter("redirect")

        if (!redirect.isNullOrBlank() && isValidLocalUrl(redirect)) {
            // 如果是有效的本站 URL，重定向到该 URL
            response.sendRedirect(redirect)
        } else {
            // 如果不是本站 URL 或参数为空，重定向到主页
            response.sendRedirect("/")
        }
    }

    /**
     * 是不是合法的URL
     */
    private fun isValidLocalUrl(url: String): Boolean {
        // 检查 URL 是否是一个有效的本站绝对路径
        return try {
            val uri = URI(url)
            // 检查是否以斜杠开头，且没有域名信息（即没有scheme和host部分）
            uri.path.startsWith("/") && uri.scheme == null && uri.host == null
        } catch (e: URISyntaxException) {
            false // 如果 URI 格式错误，则视为无效
        }
    }
}
