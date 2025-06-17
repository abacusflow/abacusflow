package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseOrderRepository : JpaRepository<PurchaseOrder, Long> {
    fun findBySupplierId(supplierId: Long): List<PurchaseOrder>
}
