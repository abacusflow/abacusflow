package org.bruwave.abacusflow.transaction

class SaleOrderCreatedEvent(
    val order: SaleOrder,
)

class SaleOrderCompletedEvent(
    val order: SaleOrder,
)

class SaleOrderItemChangedEvent(
    val order: SaleOrder,
)
