package org.abacusflow.portal.web.partner

import org.abacusflow.portal.web.model.BasicCustomerVO
import org.abacusflow.portal.web.model.CustomerVO
import org.abacusflow.usecase.partner.BasicCustomerTO
import org.abacusflow.usecase.partner.CustomerTO

fun BasicCustomerTO.toBasicVO(): BasicCustomerVO =
    BasicCustomerVO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        totalOrderCount = totalOrderCount,
        totalOrderAmount = totalOrderAmount.toDouble(),
        lastOrderDate = lastOrderDate,
    )

fun CustomerTO.toVO(): CustomerVO =
    CustomerVO(
        id,
        name,
        createdAt.toEpochMilli(),
        updatedAt.toEpochMilli(),
        phone = phone,
        address = address,
    )
