package org.bruwave.abacusflow.usecase.warehouse

import java.time.Instant

data class WarehouseTO(
    val id: Long,
    val name: String,
    val location: String?,
    val capacity: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) 