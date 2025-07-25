package org.abacusflow.usecase.user

data class UserDetailsForLoginTO(
    val id: Long,
    val name: String,
    val nick: String,
    val password: String,
    // TODO List<Role>
    val roleNames: List<String>,
    val enabled: Boolean,
    val locked: Boolean,
)
