package org.bruwave.abacusflow.portal.web.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bruwave.abacusflow.portal.web.model.ErrorVO
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.text.contains

@Component
class MyAuthenticationEntryPointHandler(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException?
    ) {
        val acceptHeader = request.getHeader("Accept")

        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
            // 返回JSON格式的错误信息
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()

            val errorResponse = ErrorVO(
                HttpServletResponse.SC_UNAUTHORIZED,
                exception?.message
            )
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } else {
            // 没有指定或指定接受其他类型的内容，重定向到登录页
            response.sendRedirect("/login")
        }
    }
}