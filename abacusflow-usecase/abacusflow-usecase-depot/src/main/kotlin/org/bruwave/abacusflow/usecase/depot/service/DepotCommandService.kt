package org.bruwave.abacusflow.usecase.depot.service

import org.bruwave.abacusflow.usecase.depot.CreateDepotInputTO
import org.bruwave.abacusflow.usecase.depot.DepotTO
import org.bruwave.abacusflow.usecase.depot.UpdateDepotInputTO

interface DepotCommandService {
    fun createDepot(input: CreateDepotInputTO): DepotTO

    fun updateDepot(
        id: Long,
        input: UpdateDepotInputTO,
    ): DepotTO

    fun deleteDepot(id: Long): DepotTO
}
