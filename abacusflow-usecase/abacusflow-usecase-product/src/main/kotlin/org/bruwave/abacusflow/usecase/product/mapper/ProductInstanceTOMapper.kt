package org.bruwave.abacusflow.usecase.product.mapper

import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductInstance
import org.bruwave.abacusflow.product.ProductUnit
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.springframework.data.util.TypeUtils.type
import java.util.UUID

fun ProductInstance.toBasicTO(productName: String, purchaseOrderNo: UUID, saleOrderNo: UUID?): BasicProductInstanceTO =
    BasicProductInstanceTO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        productName = productName,
        purchaseOrderNo = purchaseOrderNo,
        saleOrderNo = saleOrderNo
    )
