package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.transaction.SaleOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SaleOrderRepository : JpaRepository<SaleOrder, Long> {
    fun findByCustomerId(customerId: Long): List<SaleOrder>
    fun findByStatusAndOrderDateBefore(status: OrderStatus, daysAgo: LocalDate): List<SaleOrder>


    @Query(
        """
    SELECT SUM(i.quantity)
    FROM SaleOrder o
    JOIN o.items i
    WHERE o.status = :status AND i.productId = :productId
    """
    )
    fun sumItemQuantityByProductId(@Param("productId") productId: Long, @Param("status") status: OrderStatus): Long?
}
