package org.abacusflow.portal.web.depot

import org.abacusflow.portal.web.model.BasicDepotVO
import org.abacusflow.portal.web.model.DepotVO
import org.abacusflow.usecase.depot.BasicDepotTO
import org.abacusflow.usecase.depot.DepotTO

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
