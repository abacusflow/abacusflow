package org.bruwave.abacusflow.db.inventory

import org.bruwave.abacusflow.inventory.InventoryUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InventoryUnitRepository : JpaRepository<InventoryUnit, Long> {
    fun findByInventoryId(inventoryId: Long): List<InventoryUnit>

    fun findByInventoryProductId(productId: Long): List<InventoryUnit>

    fun findByIdAndStatus(
        id: Long,
        status: InventoryUnit.InventoryUnitStatus,
    ): List<InventoryUnit>

    fun findByDepotId(depotId: Long): List<InventoryUnit>

    fun findByPurchaseOrderId(orderId: Long): List<InventoryUnit>


    @Query(
        value = """
        SELECT * FROM inventory_unit 
        WHERE :saleOrderId = ANY(sale_order_ids)
    """,
        nativeQuery = true
    )
    fun findAllBySaleOrderId(saleOrderId: Long): List<InventoryUnit>
}
