package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryQueryService {
    fun listInventoriesPage(
        pageable: Pageable,
        productCategoryId: Long?,
        productName: String?,
        productType: String?,
        depotName: String?,
    ): Page<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO
}
