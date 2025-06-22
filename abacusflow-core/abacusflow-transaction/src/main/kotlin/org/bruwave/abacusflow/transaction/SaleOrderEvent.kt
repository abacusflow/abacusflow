package org.bruwave.abacusflow.transaction

class SaleOrderCreatedEvent(
    val order: SaleOrder,
)

class SaleOrderCompletedEvent(
    val order: SaleOrder,
)

class SaleOrderCanceledEvent(
    val order: SaleOrder,
)

class SaleOrderReversedEvent(
    val order: SaleOrder,
)
