package org.bruwave.abacusflow.transaction

class PurchaseOrderCreatedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderCompletedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderCanceledEvent(
    val order: PurchaseOrder,
)
class PurchaseOrderReversedEvent(
    val order: PurchaseOrder,
)
