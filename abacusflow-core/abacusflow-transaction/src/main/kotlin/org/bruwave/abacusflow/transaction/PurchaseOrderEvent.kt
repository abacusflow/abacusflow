package org.bruwave.abacusflow.transaction

class PurchaseOrderCreatedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderCompletedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderItemChangedEvent(
    val order: PurchaseOrder,
)

