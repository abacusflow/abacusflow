package org.bruwave.abacusflow.usecase.user.service

import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.CreateUserInputTO
import org.bruwave.abacusflow.usecase.user.UpdateUserInputTO
import org.bruwave.abacusflow.usecase.user.UserDetailsForLoginTO
import org.bruwave.abacusflow.usecase.user.UserTO

interface UserService {
    fun createUser(input: CreateUserInputTO): UserTO

    fun updateUser(
        id: Long,
        input: UpdateUserInputTO,
    ): UserTO

    fun deleteUser(id: Long): UserTO

    fun getUser(id: Long): UserTO?

    fun getUser(name: String): UserTO?

    fun listUsers(): List<BasicUserTO>
}
