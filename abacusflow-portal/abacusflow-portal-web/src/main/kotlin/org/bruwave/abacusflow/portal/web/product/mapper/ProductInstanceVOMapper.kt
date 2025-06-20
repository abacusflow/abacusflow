package org.bruwave.abacusflow.portal.web.product.mapper

import org.bruwave.abacusflow.portal.web.model.BasicProductInstanceVO
import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.ProductTypeVO
import org.bruwave.abacusflow.portal.web.model.ProductUnitVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.springframework.data.util.TypeUtils.type

fun BasicProductInstanceTO.toVO(): BasicProductInstanceVO =
    BasicProductInstanceVO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        purchaseOrderNo = purchaseOrderNo.toString(),
        saleOrderNo = saleOrderNo?.toString()
    )
