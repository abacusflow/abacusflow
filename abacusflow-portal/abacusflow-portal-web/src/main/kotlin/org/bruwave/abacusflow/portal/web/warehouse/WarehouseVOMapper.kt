package org.bruwave.abacusflow.portal.web.warehouse

import org.bruwave.abacusflow.portal.web.model.BasicWarehouseVO
import org.bruwave.abacusflow.portal.web.model.WarehouseVO
import org.bruwave.abacusflow.usecase.warehouse.BasicWarehouseTO
import org.bruwave.abacusflow.usecase.warehouse.WarehouseTO

fun BasicWarehouseTO.toBasicTO(): BasicWarehouseVO =
    BasicWarehouseVO(
        id = id,
        name = name,
        location = location,
        capacity = capacity,
        enabled = enabled,
    )

fun WarehouseTO.toTO(): WarehouseVO =
    WarehouseVO(
        id = id,
        name = name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        location = location,
        capacity = capacity,
        enabled = enabled,
    )
