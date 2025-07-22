package org.abacusflow.usecase.partner.mapper

import org.abacusflow.partner.Supplier
import org.abacusflow.usecase.partner.SupplierTO

fun Supplier.toTO() =
    SupplierTO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
