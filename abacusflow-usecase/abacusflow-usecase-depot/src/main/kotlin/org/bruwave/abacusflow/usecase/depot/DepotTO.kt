package org.bruwave.abacusflow.usecase.depot

import java.time.Instant

data class DepotTO(
    val id: Long,
    val name: String,
    val location: String?,
    val capacity: Int,
    val enabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
