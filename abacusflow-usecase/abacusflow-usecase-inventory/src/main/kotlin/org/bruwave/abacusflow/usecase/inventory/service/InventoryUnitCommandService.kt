package org.bruwave.abacusflow.usecase.inventory.service

interface InventoryUnitCommandService {
    fun assignDepot(id: Long, newDepotId: Long)
}