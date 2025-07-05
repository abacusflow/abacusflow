package org.bruwave.abacusflow.db.inventory

import org.bruwave.abacusflow.inventory.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Inventory?

    fun countInventoryByProductId(productId: Long): Long
}
