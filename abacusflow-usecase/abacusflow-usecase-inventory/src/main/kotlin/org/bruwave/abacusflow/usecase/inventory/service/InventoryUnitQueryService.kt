package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO

interface InventoryUnitQueryService {
    fun listInventoryUnits(): List<BasicInventoryUnitTO>

    fun getInventoryUnit(id: Long): InventoryUnitTO?
}