package org.abacusflow.db.transaction

import org.abacusflow.transaction.SaleOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleOrderItemRepository : JpaRepository<SaleOrderItem, Long> {
    fun countSaleOrderItemByInventoryUnitIdIn(ids: List<Long>): Long
}
