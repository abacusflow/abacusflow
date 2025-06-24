package org.bruwave.abacusflow.usecase.depot.service

import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.CreateDepotInputTO
import org.bruwave.abacusflow.usecase.depot.DepotTO
import org.bruwave.abacusflow.usecase.depot.UpdateDepotInputTO

interface DepotService {
    fun createDepot(input: CreateDepotInputTO): DepotTO

    fun updateDepot(
        id: Long,
        input: UpdateDepotInputTO,
    ): DepotTO

    fun deleteDepot(id: Long): DepotTO

    fun getDepot(id: Long): DepotTO

    fun listDepots(): List<BasicDepotTO>
}