package org.bruwave.abacusflow.usecase.partner

import java.time.Instant

data class CustomerTO(
    val id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) 