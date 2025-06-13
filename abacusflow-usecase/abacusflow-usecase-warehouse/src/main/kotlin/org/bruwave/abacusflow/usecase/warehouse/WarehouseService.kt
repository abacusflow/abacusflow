package org.bruwave.abacusflow.usecase.warehouse

interface WarehouseService {
    fun createWarehouse(input: CreateWarehouseInputTO): WarehouseTO
    fun updateWarehouse(id: Long, input: UpdateWarehouseInputTO): WarehouseTO
    fun deleteWarehouse(id: Long): WarehouseTO
    fun getWarehouse(id: Long): WarehouseTO
    fun listWarehouses(): List<BasicWarehouseTO>
} 