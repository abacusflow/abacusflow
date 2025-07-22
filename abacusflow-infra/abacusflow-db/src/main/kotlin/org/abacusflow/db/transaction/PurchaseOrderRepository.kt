package org.abacusflow.db.transaction

import org.abacusflow.transaction.OrderStatus
import org.abacusflow.transaction.PurchaseOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PurchaseOrderRepository : JpaRepository<PurchaseOrder, Long> {
    fun findBySupplierId(supplierId: Long): List<PurchaseOrder>

    fun findByStatusAndOrderDateBefore(
        status: OrderStatus,
        daysAgo: LocalDate,
    ): List<PurchaseOrder>
}
