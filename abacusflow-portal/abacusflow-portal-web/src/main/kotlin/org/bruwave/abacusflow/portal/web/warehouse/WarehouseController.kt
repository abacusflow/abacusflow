package org.bruwave.abacusflow.portal.web.warehouse

import org.bruwave.abacusflow.portal.web.api.WarehousesApi
import org.bruwave.abacusflow.portal.web.model.BasicWarehouseVO
import org.bruwave.abacusflow.portal.web.model.CreateWarehouseInputVO
import org.bruwave.abacusflow.portal.web.model.WarehouseVO
import org.bruwave.abacusflow.portal.web.model.UpdateWarehouseInputVO
import org.bruwave.abacusflow.usecase.warehouse.CreateWarehouseInputTO
import org.bruwave.abacusflow.usecase.warehouse.UpdateWarehouseInputTO
import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WarehouseController(
    private val warehouseService: WarehouseService
) : WarehousesApi {

    override fun listWarehouses(): ResponseEntity<List<BasicWarehouseVO>> {
        val warehouseVOs = warehouseService.listWarehouses().map { warehouse ->
            warehouse.toBasicTO()
        }
        return ResponseEntity.ok(warehouseVOs)
    }

    override fun getWarehouse(id: Long): ResponseEntity<WarehouseVO> {
        val warehouse = warehouseService.getWarehouse(id)
        return ResponseEntity.ok(
            warehouse.toTO()
        )
    }

    override fun addWarehouse(createWarehouseInputVO: CreateWarehouseInputVO): ResponseEntity<WarehouseVO> {
        val warehouse = warehouseService.createWarehouse(
            CreateWarehouseInputTO(
                createWarehouseInputVO.name,
                createWarehouseInputVO.location,
                createWarehouseInputVO.capacity
            )
        )
        return ResponseEntity.ok(
            warehouse.toTO()
        )
    }

    override fun updateWarehouse(
        id: Long,
        updateWarehouseInputVO: UpdateWarehouseInputVO
    ): ResponseEntity<WarehouseVO> {
        val warehouse = warehouseService.updateWarehouse(
            id,
            UpdateWarehouseInputTO(
                name = updateWarehouseInputVO.name,
                location = updateWarehouseInputVO.location,
                capacity = updateWarehouseInputVO.capacity
            )
        )
        return ResponseEntity.ok(
            warehouse.toTO()
        )
    }

    override fun deleteWarehouse(id: Long): ResponseEntity<Unit> {
        warehouseService.deleteWarehouse(id)
        return ResponseEntity.ok().build()
    }
}