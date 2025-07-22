package org.abacusflow.usecase.user.service

import org.abacusflow.usecase.user.CreateUserInputTO
import org.abacusflow.usecase.user.UpdateUserInputTO
import org.abacusflow.usecase.user.UserTO

interface UserCommandService {
    fun createUser(input: CreateUserInputTO): UserTO

    fun updateUser(
        id: Long,
        input: UpdateUserInputTO,
    ): UserTO

    fun deleteUser(id: Long): UserTO
}
