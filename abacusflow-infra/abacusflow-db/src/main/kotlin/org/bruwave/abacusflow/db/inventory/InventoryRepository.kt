package org.bruwave.abacusflow.db.inventory

import org.bruwave.abacusflow.inventory.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductIdAndWarehouseId(
        productId: Long,
        warehouseId: Long,
    ): Inventory?

    fun findByProductId(productId: Long): List<Inventory>

    fun findByWarehouseId(warehouseId: Long): List<Inventory>
}
