package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitForExportTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitWithTitleTO

interface InventoryUnitQueryService {
    fun listBasicInventoryUnits(): List<BasicInventoryUnitTO>

    fun listInventoryUnits(): List<InventoryUnitTO>

    fun listInventoryUnitsForExport(): List<InventoryUnitForExportTO>

    fun listInventoryUnitsWithTitle(statusList: List<String>? = null): List<InventoryUnitWithTitleTO>

    fun getInventoryUnit(id: Long): InventoryUnitTO?
}
