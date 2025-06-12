package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventories")
class InventoryController(private val inventoryService: InventoryService) {

    @GetMapping
    fun listInventories(): List<InventoryTO> = inventoryService.listInventories()

    @PostMapping
    fun createInventory(@RequestBody inventory: InventoryTO): InventoryTO = inventoryService.createInventory(inventory)

    @GetMapping("/{id}")
    fun getInventory(@PathVariable id: Long): InventoryTO = inventoryService.getInventory(id)

    @PutMapping("/{id}")
    fun updateInventory(@PathVariable id: Long, @RequestBody inventoryTO: InventoryTO): InventoryTO {
        return inventoryService.updateInventory(inventoryTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteInventory(@PathVariable id: Long): InventoryTO {
        return inventoryService.deleteInventory(InventoryTO(id = id, productId = 0, warehouseId = 0, quantity = 0, safetyStock = 0, version = 0, createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 