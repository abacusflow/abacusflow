package org.bruwave.abacusflow.usecase.depot

data class BasicDepotTO(
    val id: Long,
    val name: String,
    val location: String?,
    val capacity: Int,
    val enabled: Boolean,
)
