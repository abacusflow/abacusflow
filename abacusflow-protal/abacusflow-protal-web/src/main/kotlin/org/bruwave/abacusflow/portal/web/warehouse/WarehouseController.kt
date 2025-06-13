package org.bruwave.abacusflow.portal.web.warehouse

import org.bruwave.abacusflow.portal.web.api.WarehousesApi
import org.bruwave.abacusflow.portal.web.model.WarehouseVO
import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WarehouseController(
    private val warehouseService: WarehouseService
) : WarehousesApi{
    override fun listWarehouses(): ResponseEntity<List<WarehouseVO>> {
        return super.listWarehouses()
    }

    override fun getWarehouse(id: Long): ResponseEntity<WarehouseVO> {
        return super.getWarehouse(id)
    }

    override fun addWarehouse(warehouseVO: WarehouseVO): ResponseEntity<WarehouseVO> {
        return super.addWarehouse(warehouseVO)
    }

    override fun updateWarehouse(id: Long, warehouseVO: WarehouseVO): ResponseEntity<WarehouseVO> {
        return super.updateWarehouse(id, warehouseVO)
    }

    override fun deleteWarehouse(id: Long): ResponseEntity<WarehouseVO> {
        return super.deleteWarehouse(id)
    }
}