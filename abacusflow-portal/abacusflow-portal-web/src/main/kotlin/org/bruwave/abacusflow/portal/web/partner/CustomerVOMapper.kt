package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.model.BasicCustomerVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CustomerTO

fun BasicCustomerTO.toBasicVO(): BasicCustomerVO = BasicCustomerVO(
    id,
    name,
    phone
)

fun CustomerTO.toVO(): CustomerVO = CustomerVO(
    id,
    name,
    createdAt.toEpochMilli(),
    updatedAt.toEpochMilli(),
    phone,
    null,//TODO-NULL
    address,
)