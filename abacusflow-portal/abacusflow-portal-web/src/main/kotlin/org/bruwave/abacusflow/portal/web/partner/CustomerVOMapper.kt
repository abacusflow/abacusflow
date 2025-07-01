package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.model.BasicCustomerVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CustomerTO

fun BasicCustomerTO.toBasicVO(): BasicCustomerVO =
    BasicCustomerVO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        totalOrderCount = totalOrderCount,
        totalOrderAmount = totalOrderAmount.toDouble(),
        lastOrderTime = lastOrderTime?.toEpochMilli(),
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
