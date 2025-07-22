package org.abacusflow.usecase.user

data class CreateUserInputTO(
    val name: String,
    val nick: String,
    val sex: String? = null,
    val age: Int? = null,
)

data class UpdateUserInputTO(
    val nick: String? = null,
    val sex: String? = null,
    val age: Int? = null,
)
