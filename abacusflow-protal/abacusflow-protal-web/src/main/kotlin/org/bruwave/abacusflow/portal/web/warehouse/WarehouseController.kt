package org.bruwave.abacusflow.portal.web.warehouse

import org.bruwave.abacusflow.portal.web.api.WarehousesApi
import org.bruwave.abacusflow.portal.web.model.BasicWarehouseVO
import org.bruwave.abacusflow.portal.web.model.CreateWarehouseInputVO
import org.bruwave.abacusflow.portal.web.model.UpdateWarehouseInputVO
import org.bruwave.abacusflow.portal.web.model.WarehouseVO
import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WarehouseController(
    private val warehouseService: WarehouseService
) : WarehousesApi{
    override fun listWarehouses(): ResponseEntity<List<BasicWarehouseVO>> {
        return super.listWarehouses()
    }

    override fun getWarehouse(id: Long): ResponseEntity<WarehouseVO> {
        return super.getWarehouse(id)
    }

    override fun addWarehouse(createWarehouseInputVO: CreateWarehouseInputVO): ResponseEntity<WarehouseVO> {
        return super.addWarehouse(createWarehouseInputVO)
    }

    override fun updateWarehouse(
        id: Long,
        updateWarehouseInputVO: UpdateWarehouseInputVO
    ): ResponseEntity<WarehouseVO> {
        return super.updateWarehouse(id, updateWarehouseInputVO)
    }

    override fun deleteWarehouse(id: Long): ResponseEntity<WarehouseVO> {
        return super.deleteWarehouse(id)
    }
}