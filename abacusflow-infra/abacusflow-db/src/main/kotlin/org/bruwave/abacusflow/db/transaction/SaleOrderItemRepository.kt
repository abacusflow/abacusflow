package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SaleOrderItemRepository : JpaRepository<SaleOrderItem, Long> {
}
