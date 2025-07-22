package org.abacusflow.portal.web.partner

import org.abacusflow.portal.web.model.BasicSupplierVO
import org.abacusflow.portal.web.model.SupplierVO
import org.abacusflow.usecase.partner.BasicSupplierTO
import org.abacusflow.usecase.partner.SupplierTO

fun BasicSupplierTO.toBasicVO(): BasicSupplierVO =
    BasicSupplierVO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        address = address,
        totalOrderCount = totalOrderCount,
        totalOrderAmount = totalOrderAmount.toDouble(),
        lastOrderDate = lastOrderDate,
    )

fun SupplierTO.toVO(): SupplierVO =
    SupplierVO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
    )
