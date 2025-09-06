package org.abacusflow.portal.web.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.abacusflow.portal.web.model.ErrorResponseVO
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets

@Component
class AbacusFlowAuthenticationEntryPointHandler(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        // 1. 检查是否为AJAX/API请求
        val isApiRequest = isApiCall(request)

        if (isApiRequest) {
            // API请求：返回JSON格式的401错误
            sendJsonError(response, exception)
        } else {
            // 非API请求：重定向到登录页
            response.sendRedirect("/login")
        }
    }

    private fun isApiCall(request: HttpServletRequest): Boolean {
        // 检查标准API请求头
        val acceptHeader = request.getHeader("Accept") ?: ""
        val contentType = request.getHeader("Content-Type") ?: ""
        val requestedWith = request.getHeader("X-Requested-With") ?: ""

        // 判断标准：接受JSON内容 或 是AJAX请求 或 内容类型为JSON
        return acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) ||
            requestedWith.equals("XMLHttpRequest", ignoreCase = true) ||
            contentType.contains(MediaType.APPLICATION_JSON_VALUE)
    }

    private fun sendJsonError(
        response: HttpServletResponse,
        exception: AuthenticationException?,
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val errorResponse =
            ErrorResponseVO(
                code = HttpServletResponse.SC_UNAUTHORIZED,
                message = exception?.message ?: "Authentication required",
            )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }
}
