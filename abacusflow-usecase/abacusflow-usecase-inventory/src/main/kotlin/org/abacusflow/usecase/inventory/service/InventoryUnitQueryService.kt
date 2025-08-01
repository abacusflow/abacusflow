package org.abacusflow.usecase.inventory.service

import org.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.abacusflow.usecase.inventory.InventoryUnitForExportTO
import org.abacusflow.usecase.inventory.InventoryUnitTO
import org.abacusflow.usecase.inventory.InventoryUnitWithTitleTO
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

    fun listInventoryUnitsForExport(productCategoryId: Long?): List<InventoryUnitForExportTO>

    fun listInventoryUnitsWithTitle(statusList: List<String>? = null): List<InventoryUnitWithTitleTO>

    fun getInventoryUnit(id: Long): InventoryUnitTO?
}
