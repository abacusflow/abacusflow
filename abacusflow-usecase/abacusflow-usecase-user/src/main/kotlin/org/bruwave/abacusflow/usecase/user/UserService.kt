package org.bruwave.abacusflow.usecase.user

interface UserService {
    fun createUser(input: CreateUserInputTO): UserTO

    fun updateUser(
        id: Long,
        input: UpdateUserInputTO,
    ): UserTO

    fun deleteUser(id: Long): UserTO

    fun getUser(id: Long): UserTO

    fun getUser(name: String): UserTO

    fun listUsers(): List<BasicUserTO>
}
