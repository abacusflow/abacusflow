package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderItemTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO

fun SaleOrder.toTO() = SaleOrderTO(
    id = id,
    customerId = customerId,
    status = status.name,
    items = items.map { it.toTO() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    orderNo = orderNo,
    orderDate = orderDate,
    note = note
)

fun SaleOrder.toBasicTO(customerName: String) =
    BasicSaleOrderTO(
        id = id,
        status = status.name,
        itemCount = items.count(),
        createdAt = createdAt,
        orderNo = orderNo,
        customerName = customerName,
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        orderDate = orderDate,
    )

fun SaleOrderItem.toTO() = SaleOrderItemTO(
    id = id,
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal
)