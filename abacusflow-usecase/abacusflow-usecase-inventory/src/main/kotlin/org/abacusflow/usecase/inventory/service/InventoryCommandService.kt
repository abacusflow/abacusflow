package org.abacusflow.usecase.inventory.service

import org.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.abacusflow.usecase.inventory.InventoryTO

interface InventoryCommandService {
    fun createInventory(input: CreateInventoryInputTO): InventoryTO

    fun adjustWarningLine(
        id: Long,
        newSafetyStock: Long,
        newMaxStock: Long,
    )

    fun checkSafetyStock(id: Long): Boolean
}
