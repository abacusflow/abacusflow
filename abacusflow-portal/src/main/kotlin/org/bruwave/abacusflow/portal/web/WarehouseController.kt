package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.warehouse.WarehouseService
import org.bruwave.abacusflow.usecase.warehouse.WarehouseTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/warehouses")
class WarehouseController(private val warehouseService: WarehouseService) {

    @GetMapping
    fun listWarehouses(): List<WarehouseTO> = warehouseService.listWarehouses()

    @PostMapping
    fun createWarehouse(@RequestBody warehouse: WarehouseTO): WarehouseTO = warehouseService.createWarehouse(warehouse)

    @GetMapping("/{id}")
    fun getWarehouse(@PathVariable id: Long): WarehouseTO = warehouseService.getWarehouse(id)

    @PutMapping("/{id}")
    fun updateWarehouse(@PathVariable id: Long, @RequestBody warehouseTO: WarehouseTO): WarehouseTO {
        return warehouseService.updateWarehouse(warehouseTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteWarehouse(@PathVariable id: Long): WarehouseTO {
        return warehouseService.deleteWarehouse(WarehouseTO(id = id, name = "", location = null, capacity = 0, createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 