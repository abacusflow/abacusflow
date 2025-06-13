package org.bruwave.abacusflow.usecase.inventory

interface InventoryService {
    //TODO 创建库存实体是只要有新产品就会创建
    fun createInventory(to: InventoryTO): InventoryTO
    fun increaseInventory(id: Long, amount: Int): InventoryTO
    fun decreaseInventory(id: Long, amount: Int): InventoryTO
    fun getInventory(id: Long): InventoryTO
    fun listInventories(): List<BasicInventoryTO>
    fun checkSafetyStock(id: Long): Boolean
}
