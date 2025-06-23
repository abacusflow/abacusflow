package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

interface InventoryQueryService {
    fun listInventories(): List<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO
}