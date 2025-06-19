package org.bruwave.abacusflow.transaction

class PurchaseOrderCreatedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderCompletedEvent(
    val order: PurchaseOrder,
)

class PurchaseOrderItemChangedEvent(
    val items: List<PurchaseOrderItem>,
)

class SaleOrderCreatedEvent(
    val order: SaleOrder,
)

class SaleOrderCompletedEvent(
    val order: SaleOrder,
)

class SaleOrderItemChangedEvent(
    val items: List<SaleOrderItem>,
)
