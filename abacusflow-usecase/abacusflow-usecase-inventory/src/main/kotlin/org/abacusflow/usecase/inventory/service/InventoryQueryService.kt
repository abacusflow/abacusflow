package org.abacusflow.usecase.inventory.service

import org.abacusflow.usecase.inventory.BasicInventoryTO
import org.abacusflow.usecase.inventory.InventoryTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryQueryService {
    fun listBasicInventoriesPage(
        pageable: Pageable,
        productCategoryId: Long?,
        productName: String?,
        productType: String?,
        inventoryUnitCode: String?,
        depotName: String?,
    ): Page<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO
}
