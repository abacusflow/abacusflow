package org.bruwave.abacusflow.usecase.inventory

data class CreateInventoryInputTO(
    val productId: Long,
    val warehouseId: Long,
    val quantity: Int,
    val safetyStock: Int
)

data class UpdateInventoryInputTO(
    val quantity: Int,
    val safetyStock: Int
)