package org.bruwave.abacusflow.usecase.inventory.service

import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryQueryService {
    fun queryPagedInventories(
        pageable: Pageable,
        productCategoryId: Long?,
        productId: Long?,
        depotId: Long?
    ): Page<BasicInventoryTO>

    fun getInventory(id: Long): InventoryTO
}
