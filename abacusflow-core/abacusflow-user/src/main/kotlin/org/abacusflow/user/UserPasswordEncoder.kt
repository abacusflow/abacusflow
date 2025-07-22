package org.abacusflow.user

interface UserPasswordEncoder {
    fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean

    fun encode(password: String): String
}
