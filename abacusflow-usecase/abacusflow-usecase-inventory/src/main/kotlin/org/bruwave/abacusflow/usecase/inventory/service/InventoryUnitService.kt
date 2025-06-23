package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

interface InventoryUnitService {
    fun listInventoryUnits(): List<BasicInventoryUnitTO>
}