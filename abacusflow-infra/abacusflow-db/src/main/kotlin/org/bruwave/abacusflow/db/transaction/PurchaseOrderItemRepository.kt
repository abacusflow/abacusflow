package org.bruwave.abacusflow.db.transaction

import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseOrderItemRepository : JpaRepository<PurchaseOrderItem, Long> {
    fun findByProductId(productId: Long): List<PurchaseOrderItem>

    // TODO 应该是在PurchaseOrder作为切入点统计呢？还是在PurchaseOrderItem加上orderId 在这统计呢？
//    @Query(
//        """
//    SELECT SUM(p.quantity)
//    FROM PurchaseOrderItem p
//    JOIN PurchaseOrder o ON p.orderId = o.id
//    WHERE p.productId = :productId AND o.status = 'COMPLETED'
//    """
//    )
//    fun findTotalQuantityByProductId(@Param("productId") productId: Long): Long?
}
