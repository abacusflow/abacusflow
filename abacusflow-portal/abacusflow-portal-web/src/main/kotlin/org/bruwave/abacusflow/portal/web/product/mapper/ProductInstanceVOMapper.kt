package org.bruwave.abacusflow.portal.web.product.mapper

import org.bruwave.abacusflow.portal.web.model.BasicProductInstanceVO
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO

fun BasicProductInstanceTO.toVO(): BasicProductInstanceVO =
    BasicProductInstanceVO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        purchaseOrderNo = purchaseOrderNo.toString(),
        saleOrderNo = saleOrderNo?.toString(),
    )
