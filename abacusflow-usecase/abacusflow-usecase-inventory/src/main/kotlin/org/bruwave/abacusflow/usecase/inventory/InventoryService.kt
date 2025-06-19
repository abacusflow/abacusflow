package org.bruwave.abacusflow.usecase.inventory

interface InventoryService {
    fun listInventories(): List<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO

    // TODO 创建库存实体是只要有新产品就会创建
    fun createInventory(input: CreateInventoryInputTO): InventoryTO

    fun increaseInventory(
        id: Long,
        amount: Int,
    )

    fun decreaseInventory(
        id: Long,
        amount: Int,
    )

    fun reserveInventory(
        id: Long,
        amount: Int,
    )

    fun assignDepot(
        id: Long,
        newDepotId: Long,
    )

    fun adjustWarningLine(
        id: Long,
        newSafetyStock: Long,
        newMaxStock: Long,
    )

    fun checkSafetyStock(id: Long): Boolean
}
