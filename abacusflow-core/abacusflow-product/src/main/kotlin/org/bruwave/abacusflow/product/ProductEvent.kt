package org.bruwave.abacusflow.product

class ProductCreatedEvent(
    val product: Product,
)

class ProductDeletedEvent(
    val product: Product,
)

class ProductUpdatedEvent(
    val productId: Long,
)
