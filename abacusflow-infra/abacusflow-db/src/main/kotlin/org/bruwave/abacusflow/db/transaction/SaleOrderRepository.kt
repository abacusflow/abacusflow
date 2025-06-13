package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.SaleOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleOrderRepository : JpaRepository<SaleOrder, Long> {
    fun findByCustomerId(customerId: Long): List<SaleOrder>
}