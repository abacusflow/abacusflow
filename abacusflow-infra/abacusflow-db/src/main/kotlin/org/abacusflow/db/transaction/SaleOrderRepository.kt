package org.abacusflow.db.transaction

import org.abacusflow.transaction.OrderStatus
import org.abacusflow.transaction.SaleOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SaleOrderRepository : JpaRepository<SaleOrder, Long> {
    fun findByCustomerId(customerId: Long): List<SaleOrder>

    fun findByStatusAndOrderDateBefore(
        status: OrderStatus,
        daysAgo: LocalDate,
    ): List<SaleOrder>
}
