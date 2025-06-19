package org.bruwave.abacusflow.usecase.inventory

data class CreateInventoryInputTO(
    val productId: Long,
    val depotId: Long,
    val quantity: Int,
)
