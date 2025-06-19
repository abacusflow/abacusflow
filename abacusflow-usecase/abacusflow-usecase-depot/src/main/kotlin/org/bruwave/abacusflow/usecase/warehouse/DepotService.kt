package org.bruwave.abacusflow.usecase.depot

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
