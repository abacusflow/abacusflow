package org.bruwave.abacusflow.transaction

import java.util.UUID

class SaleOrderCreatedEvent(
    val order: SaleOrder,
)

class SaleOrderCompletedEvent(
    val order: SaleOrder,
)

class SaleOrderItemChangedEvent(
    val orderId: Long,
    val orderNo: UUID,
    val items: List<SaleOrderItem>,
)
