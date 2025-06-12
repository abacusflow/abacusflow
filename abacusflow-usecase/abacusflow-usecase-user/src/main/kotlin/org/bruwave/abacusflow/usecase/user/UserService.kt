package org.bruwave.abacusflow.usecase.user

interface UserService {
    fun createUser(input: UserInputTO): UserTO

    fun updateUser(input: UserInputTO): UserTO

    fun deleteUser(id: Long): UserTO

    fun getUser(id: Long): UserTO

    fun getUser(name: String): UserTO

    fun listUsers(): List<BasicUserTO>
}
