package org.abacusflow.db.inventory

import org.abacusflow.inventory.InventoryUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InventoryUnitRepository : JpaRepository<InventoryUnit, Long> {
    fun findByInventoryId(inventoryId: Long): List<InventoryUnit>

    fun findByInventoryProductId(productId: Long): List<InventoryUnit>

    fun findByIdAndStatusIn(
        id: Long,
        statuses: List<InventoryUnit.InventoryUnitStatus>,
    ): List<InventoryUnit>

    fun findByDepotId(depotId: Long): List<InventoryUnit>

    fun findByPurchaseOrderId(orderId: Long): List<InventoryUnit>

    @Query(
        value = """
        SELECT * FROM inventory_unit 
        WHERE :saleOrderId = ANY(sale_order_ids)
    """,
        nativeQuery = true,
    )
    fun findAllBySaleOrderId(saleOrderId: Long): List<InventoryUnit>
}
