package org.bruwave.abacusflow.usecase.partner.mapper

import org.bruwave.abacusflow.partner.Customer
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CustomerTO

fun Customer.toTO() =
    CustomerTO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Customer.toBasicTO() =
    BasicCustomerTO(
        id = id,
        name = name,
        phone = phone,
        address = address,
    )
