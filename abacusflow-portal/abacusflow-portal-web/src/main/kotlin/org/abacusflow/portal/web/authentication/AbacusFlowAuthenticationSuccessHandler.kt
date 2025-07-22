package org.abacusflow.portal.web.authentication

import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.abacusflow.usecase.user.service.UserAuthenticationService
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

@Component
class AbacusFlowAuthenticationSuccessHandler(
    private val userAuthenticationService: UserAuthenticationService,
) : AuthenticationSuccessHandler {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        setCubeTokenCookie(request, response)

        val redirect = request.getParameter("redirect")

        if (!redirect.isNullOrBlank() && isValidLocalUrl(redirect)) {
            // 如果是有效的本站 URL，重定向到该 URL
            response.sendRedirect(redirect)
        } else {
            // 如果不是本站 URL 或参数为空，重定向到主页
            response.sendRedirect("/")
        }
    }

    private fun setCubeTokenCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val cookie =
            Cookie("CUBE_JWT_TOKEN", CUBE_JWT_TOKEN).apply {
                path = "/"
                isHttpOnly = false // ✅ 如果你用 JS 读取，可设为 false；若你打算前端不读、直接 header 传回，建议 true
                maxAge = 60 * 60 * 24 * 365 // 1年
//            secure = request.isSecure  // 若是 HTTPS
            }
        response.addCookie(cookie)
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

    companion object {
        // 先写死，之后会后端签发的
        const val CUBE_JWT_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTc1MTYyNDQzOH0." +
                "9PYDk96jITyoNjlmZ04by_1N_VR6HUw6h_A7jlk6m-Q"
    }
}
