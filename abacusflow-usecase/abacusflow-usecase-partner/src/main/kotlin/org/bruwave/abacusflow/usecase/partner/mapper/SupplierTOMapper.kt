package org.bruwave.abacusflow.usecase.partner.mapper

import org.bruwave.abacusflow.partner.Supplier
import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO

fun Supplier.toTO() =
    SupplierTO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Supplier.toBasicTO() =
    BasicSupplierTO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        address = address,
    )
