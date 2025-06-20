package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.model.BasicSaleOrderVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderItemInputVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderItemVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.SaleItemInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO

fun BasicSaleOrderTO.toBasicVO(): BasicSaleOrderVO =
    BasicSaleOrderVO(
        id = id,
        orderNo = orderNo.toString(),
        customerName = customerName,
        status = mapOrderStatusTOToVO(status),
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        itemCount = itemCount,
        orderDate = orderDate,
    )

fun SaleOrderTO.toVO(): SaleOrderVO =
    SaleOrderVO(
        id = id,
        orderNo = orderNo.toString(),
        customerId = customerId,
        status = mapOrderStatusTOToVO(status),
        orderItems = items.map { it.toVO() },
        note = note,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        orderDate = orderDate,
    )

fun SaleOrderTO.SaleOrderItemTO.toVO(): SaleOrderItemVO =
    SaleOrderItemVO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        subtotal = subtotal,
    )

fun SaleOrderItemInputVO.toInputTO(): SaleItemInputTO =
    SaleItemInputTO(
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        productInstanceId = productInstanceId,
    )
