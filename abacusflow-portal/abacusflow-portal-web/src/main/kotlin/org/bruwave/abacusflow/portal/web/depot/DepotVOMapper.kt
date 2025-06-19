package org.bruwave.abacusflow.portal.web.depot

import org.bruwave.abacusflow.portal.web.model.BasicDepotVO
import org.bruwave.abacusflow.portal.web.model.DepotVO
import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.DepotTO

fun BasicDepotTO.toBasicTO(): BasicDepotVO =
    BasicDepotVO(
        id = id,
        name = name,
        location = location,
        capacity = capacity,
        enabled = enabled,
    )

fun DepotTO.toTO(): DepotVO =
    DepotVO(
        id = id,
        name = name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        location = location,
        capacity = capacity,
        enabled = enabled,
    )
