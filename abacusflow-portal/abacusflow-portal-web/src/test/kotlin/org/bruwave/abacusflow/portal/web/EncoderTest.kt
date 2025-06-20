package org.bruwave.abacusflow.portal.web

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EncoderTest {
    private val encoder = BCryptPasswordEncoder()

    @Test
    fun `BCrypt加密同一密码每次结果不同但都能正确验证`() {
        // Given
        val plainPassword = "asus"

        // When
        val encoded1 = encoder.encode(plainPassword)
        val encoded2 = encoder.encode(plainPassword)

        // Then
        println("第一次加密: $encoded1")
        println("第二次加密: $encoded2")

        // 验证两次加密结果不同
        assertNotEquals(encoded1, encoded2, "BCrypt每次加密结果应该不同")

        // 验证都能正确匹配原密码
        assertTrue(encoder.matches(plainPassword, encoded1), "第一次加密的密码应该能正确验证")
        assertTrue(encoder.matches(plainPassword, encoded2), "第二次加密的密码应该能正确验证")

        // 验证错误密码不能匹配
        assertFalse(encoder.matches("wrong_password", encoded1), "错误密码不应该匹配")
        assertFalse(encoder.matches("wrong_password", encoded2), "错误密码不应该匹配")
    }
}
