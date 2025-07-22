package org.abacusflow.portal.web.product.mapper

import org.abacusflow.portal.web.model.BasicProductInstanceVO
import org.abacusflow.usecase.product.BasicProductInstanceTO

fun BasicProductInstanceTO.toVO(): BasicProductInstanceVO =
    BasicProductInstanceVO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        unitPrice = unitPrice.toDouble(),
        purchaseOrderNo = purchaseOrderNo.toString(),
        saleOrderNo = saleOrderNo?.toString(),
    )
