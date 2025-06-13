package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryService: InventoryService
): InventoriesApi{

    override fun listInventories(): ResponseEntity<List<InventoryVO>> {
        return super.listInventories()
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        return super.getInventory(id)
    }

    override fun addInventory(inventoryVO: InventoryVO): ResponseEntity<InventoryVO> {
        return super.addInventory(inventoryVO)
    }

    override fun updateInventory(id: Long, inventoryVO: InventoryVO): ResponseEntity<InventoryVO> {
        return super.updateInventory(id, inventoryVO)
    }

    override fun deleteInventory(id: Long): ResponseEntity<InventoryVO> {
        return super.deleteInventory(id)
    }
}