package org.bruwave.abacusflow.usecase.warehouse

interface WarehouseService {
    fun createWarehouse(warehouse: WarehouseTO): WarehouseTO
    fun updateWarehouse(warehouseTO: WarehouseTO): WarehouseTO
    fun deleteWarehouse(warehouseTO: WarehouseTO): WarehouseTO
    fun getWarehouse(id: Long): WarehouseTO
    fun getWarehouse(name: String): WarehouseTO
    fun listWarehouses(): List<WarehouseTO>
} 