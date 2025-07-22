package org.abacusflow.usecase.inventory.service

interface InventoryUnitCommandService {
    fun assignDepot(
        id: Long,
        newDepotId: Long,
    )
}
