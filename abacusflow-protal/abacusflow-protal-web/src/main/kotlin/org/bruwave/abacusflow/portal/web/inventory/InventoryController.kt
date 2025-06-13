package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.CreateInventoryInputVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.UpdateInventoryInputVO
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryService: InventoryService
): InventoriesApi{

    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        return super.listInventories()
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        return super.getInventory(id)
    }

    override fun addInventory(createInventoryInputVO: CreateInventoryInputVO): ResponseEntity<InventoryVO> {
        return super.addInventory(createInventoryInputVO)
    }

    override fun updateInventory(
        id: Long,
        updateInventoryInputVO: UpdateInventoryInputVO
    ): ResponseEntity<InventoryVO> {
        return super.updateInventory(id, updateInventoryInputVO)
    }

    override fun deleteInventory(id: Long): ResponseEntity<InventoryVO> {
        return super.deleteInventory(id)
    }
}