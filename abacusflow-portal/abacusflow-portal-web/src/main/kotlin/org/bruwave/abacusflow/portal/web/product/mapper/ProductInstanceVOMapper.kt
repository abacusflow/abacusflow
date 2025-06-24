package org.bruwave.abacusflow.portal.web.product.mapper

import org.bruwave.abacusflow.portal.web.model.BasicProductInstanceVO
import org.bruwave.abacusflow.portal.web.model.BasicProductInstancesInnerVO
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
import org.bruwave.abacusflow.usecase.product.ProductInstanceForBasicProductTO

fun BasicProductInstanceTO.toVO(): BasicProductInstanceVO =
    BasicProductInstanceVO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        unitPrice = unitPrice.toDouble(),
        purchaseOrderNo = purchaseOrderNo.toString(),
        saleOrderNo = saleOrderNo?.toString(),
    )

fun ProductInstanceForBasicProductTO.toVO(): BasicProductInstancesInnerVO =
    BasicProductInstancesInnerVO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        unitPrice = unitPrice.toDouble(),
    )
