package org.bruwave.abacusflow.db.inventory

import org.bruwave.abacusflow.inventory.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductIdAndDepotId(
        productId: Long,
        depotId: Long,
    ): Inventory?

    fun findByProductId(productId: Long): Inventory?

    fun findByDepotId(depotId: Long): List<Inventory>
}
