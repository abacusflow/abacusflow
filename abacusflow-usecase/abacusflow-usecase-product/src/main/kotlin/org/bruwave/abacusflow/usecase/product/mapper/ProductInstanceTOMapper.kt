package org.bruwave.abacusflow.usecase.product.mapper

import org.bruwave.abacusflow.product.ProductInstance
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
import org.bruwave.abacusflow.usecase.product.ProductInstanceForBasicProductTO
import java.util.UUID

fun ProductInstance.toBasicTO(
    purchaseOrderNo: UUID,
    saleOrderNo: UUID?,
): BasicProductInstanceTO =
    BasicProductInstanceTO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        unitPrice = unitPrice,
        productId = product.id,
        productName = product.name,
        purchaseOrderNo = purchaseOrderNo,
        saleOrderNo = saleOrderNo,
    )

fun ProductInstance.toForBasicProductTO(
): ProductInstanceForBasicProductTO =
    ProductInstanceForBasicProductTO(
        id = id,
        name = name,
        serialNumber = serialNumber,
        unitPrice = unitPrice,
        productId = product.id,
    )
