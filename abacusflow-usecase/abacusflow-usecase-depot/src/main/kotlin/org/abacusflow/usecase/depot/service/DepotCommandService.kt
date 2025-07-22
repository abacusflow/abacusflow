package org.abacusflow.usecase.depot.service

import org.abacusflow.usecase.depot.CreateDepotInputTO
import org.abacusflow.usecase.depot.DepotTO
import org.abacusflow.usecase.depot.UpdateDepotInputTO

interface DepotCommandService {
    fun createDepot(input: CreateDepotInputTO): DepotTO

    fun updateDepot(
        id: Long,
        input: UpdateDepotInputTO,
    ): DepotTO

    fun deleteDepot(id: Long): DepotTO
}
