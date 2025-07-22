package org.abacusflow.usecase.user.service.impl

import org.abacusflow.user.UserPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class DefaultUserPasswordEncoder : UserPasswordEncoder {
    private val instance = BCryptPasswordEncoder()

    override fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean {
        return instance.matches(password, encodedPassword)
    }

    override fun encode(password: String): String {
        return instance.encode(password)
    }
}
