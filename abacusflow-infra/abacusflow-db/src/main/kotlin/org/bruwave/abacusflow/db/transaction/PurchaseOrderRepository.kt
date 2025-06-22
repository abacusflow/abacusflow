package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PurchaseOrderRepository : JpaRepository<PurchaseOrder, Long> {
    fun findBySupplierId(supplierId: Long): List<PurchaseOrder>
    fun findByStatusAndOrderDateBefore(status: OrderStatus, daysAgo: LocalDate): List<PurchaseOrder>

    @Query(
        """
    SELECT SUM(i.quantity)
    FROM PurchaseOrder o
    JOIN o.items i
    WHERE o.status = :status AND i.productId = :productId
    """
    )
    fun sumItemQuantityByProductId(@Param("productId") productId: Long, @Param("status") status: OrderStatus): Long?
}
