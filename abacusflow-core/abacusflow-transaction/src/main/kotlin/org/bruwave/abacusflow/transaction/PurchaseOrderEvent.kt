package org.bruwave.abacusflow.transaction

import java.util.UUID

class PurchaseOrderCreatedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderCompletedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderItemChangedEvent(
    val orderId: Long,
    val orderNo: UUID,
    val items: List<PurchaseOrderItem>,
)

