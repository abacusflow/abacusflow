package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PurchaseOrderItemRepository : JpaRepository<PurchaseOrderItem, Long> {
    fun findByProductId(productId: Long): List<PurchaseOrderItem>

    @Query(
        """
        SELECT SUM(p.quantity) FROM PurchaseOrderItem p WHERE p.productId = :productId
    """,
    )
    fun findTotalQuantityByProductId(
        @Param("productId") productId: Long,
    ): Long?
}
