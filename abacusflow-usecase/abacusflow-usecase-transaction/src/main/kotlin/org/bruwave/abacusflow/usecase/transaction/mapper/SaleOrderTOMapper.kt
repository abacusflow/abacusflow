package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import java.time.LocalDate

fun SaleOrder.toTO() =
    SaleOrderTO(
        id = id,
        customerId = customerId,
        status = status.name,
        items = items.map { it.toTO() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        orderNo = no,
        orderDate = orderDate,
        note = note,
    )

fun SaleOrder.toBasicTO(customerName: String): BasicSaleOrderTO {
    val autoCompleteDate: LocalDate? =
        if (status == OrderStatus.PENDING) {
            orderDate.plusDays(7)
        } else {
            null
        }

    return BasicSaleOrderTO(
        id = id,
        status = status.name,
        itemCount = items.count(),
        createdAt = createdAt,
        orderNo = no,
        customerName = customerName,
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        orderDate = orderDate,
        autoCompleteDate = autoCompleteDate,
    )
}

fun SaleOrderItem.toTO() =
    SaleOrderTO.SaleOrderItemTO(
        id = id,
        quantity = quantity,
        unitPrice = unitPrice,
        discountedPrice = discountedPrice,
        subtotal = subtotal,
        inventoryUnitId = inventoryUnitId,
    )
