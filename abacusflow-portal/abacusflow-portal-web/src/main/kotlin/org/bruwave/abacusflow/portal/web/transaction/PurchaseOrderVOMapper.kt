package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.model.BasicPurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.OrderStatusVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderItemInputVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderItemVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderVO
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseItemInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderItemTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO

fun BasicPurchaseOrderTO.toBasicVO(): BasicPurchaseOrderVO = BasicPurchaseOrderVO(
    id = id,
    orderNo = orderNo.toString(),
    supplierName = supplierName,
    status = OrderStatusVO.valueOf(status),
    totalAmount = totalAmount,
    totalQuantity = totalQuantity,
    itemCount = itemCount,
    orderDate = orderDate
)

fun PurchaseOrderTO.toVO(): PurchaseOrderVO = PurchaseOrderVO(
    id = id,
    orderNo = orderNo.toString(),
    supplierId = supplierId,
    supplierName = "null",//TODO-NULL
    status = OrderStatusVO.valueOf(status),
    orderItems = items.map { it.toVO() },
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun PurchaseOrderItemTO.toVO(): PurchaseOrderItemVO = PurchaseOrderItemVO(
    id = id,
    productId = productId,
    productName = "null",//TODO-NULL
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal,
)

fun PurchaseOrderItemInputVO.toInputTO(): PurchaseItemInputTO = PurchaseItemInputTO(
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice
)