package org.bruwave.abacusflow.usecase.user

import java.time.Instant

data class BasicUserTO(
    val id: Long,
    val name: String,
    val nick: String,
    val sex: String,
    val age: Int,
    val roles: List<String>,
    val enabled: Boolean,
    val locked: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
