package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

interface InventoryService {
    fun listInventories(): List<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO

    // TODO 创建库存实体是只要有新产品就会创建
    fun createInventory(input: CreateInventoryInputTO): InventoryTO

    fun adjustWarningLine(id: Long, newSafetyStock: Long, newMaxStock: Long)

    fun checkSafetyStock(id: Long): Boolean
}