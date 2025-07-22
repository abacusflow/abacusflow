package org.abacusflow.usecase.partner.mapper

import org.abacusflow.partner.Customer
import org.abacusflow.usecase.partner.CustomerTO

fun Customer.toTO() =
    CustomerTO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
