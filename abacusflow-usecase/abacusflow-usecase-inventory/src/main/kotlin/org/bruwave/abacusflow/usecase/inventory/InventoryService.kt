package org.bruwave.abacusflow.usecase.inventory

interface InventoryService {
    fun createInventory(to: InventoryTO): InventoryTO
    fun increaseInventory(id: Long, amount: Int): InventoryTO
    fun decreaseInventory(id: Long, amount: Int): InventoryTO
    fun getInventory(id: Long): InventoryTO
    fun listInventories(): List<InventoryTO>
}
