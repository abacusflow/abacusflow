package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.model.BasicSupplierVO
import org.bruwave.abacusflow.portal.web.model.SupplierVO
import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO

fun BasicSupplierTO.toBasicVO(): BasicSupplierVO =
    BasicSupplierVO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        address = address,
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
