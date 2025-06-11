package org.bruwave.abacusflow.usecase.user

interface UserService {
    fun createUser(user: UserTO): UserTO

    fun updateUser(userTO: UserTO): UserTO

    fun deleteUser(userTO: UserTO): UserTO

    fun getUser(id: Long): UserTO

    fun getUser(name: String): UserTO

    fun listUsers(): List<BasicUserTO>
}
