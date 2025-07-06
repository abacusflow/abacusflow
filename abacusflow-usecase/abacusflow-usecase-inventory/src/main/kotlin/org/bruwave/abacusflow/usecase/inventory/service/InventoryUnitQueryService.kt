package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitForExportTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitWithTitleTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryUnitQueryService {
    fun listBasicInventoryUnits(
        pageable: Pageable,
        productCategoryId: Long?,
        productName: String?,
        productType: String?,
        inventoryUnitCode: String?,
        depotName: String?,
    ): Page<BasicInventoryUnitTO>

    fun listInventoryUnits(): List<InventoryUnitTO>

    fun listInventoryUnitsForExport(): List<InventoryUnitForExportTO>

    fun listInventoryUnitsWithTitle(statusList: List<String>? = null): List<InventoryUnitWithTitleTO>

    fun getInventoryUnit(id: Long): InventoryUnitTO?
}
