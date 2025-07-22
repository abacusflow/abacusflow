package org.abacusflow.partner

class CustomerCreatedEvent(
    val customerId: Long,
)

class CustomerUpdatedEvent(
    val customerId: Long,
)

class SupplierCreatedEvent(
    val supplierId: Long,
)

class SupplierUpdatedEvent(
    val supplierId: Long,
)
