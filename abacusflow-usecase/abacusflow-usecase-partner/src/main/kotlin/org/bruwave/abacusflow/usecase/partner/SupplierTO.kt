package org.bruwave.abacusflow.usecase.partner

import java.time.Instant

data class SupplierTO(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val phone: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
