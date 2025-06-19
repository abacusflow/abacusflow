package org.bruwave.abacusflow.inventory

class InventoryIncreasedEvent(
    val inventoryId: Long,
    val productId: Long,
    val depotId: Long,
    val amount: Int,
)

class InventoryDecreasedEvent(
    val inventoryId: Long,
    val productId: Long,
    val depotId: Long,
    val amount: Int,
)

class InventoryReservedEvent(
    val inventoryId: Long,
    val productId: Long,
    val depotId: Long,
    val amount: Int,
)

class LowStockWarningEvent(
    val inventoryId: Long,
    val productId: Long,
    val currentQuantity: Int,
    val safetyStock: Int,
)
