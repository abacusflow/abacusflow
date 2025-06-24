package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.AdjustWarningLineRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.PageBasicInventoryVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryCommandService
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryCommandService: InventoryCommandService,
    private val inventoryQueryService: InventoryQueryService,
) : InventoriesApi {
    override fun queryPagedInventories(
        pageIndex: Int,
        pageSize: Int,
        productCategoryId: Long?,
        productId: Long?,
        depotId: Long?
    ): ResponseEntity<PageBasicInventoryVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page = inventoryQueryService.queryPagedInventories(
            pageable,
            productCategoryId,
            productId,
            depotId
        ).map { it.toBasicVO() }

        val pageVO = PageBasicInventoryVO(
            content = page.content,
            totalElements = page.totalElements,
            number = page.number,
            propertySize = page.size
        )

        return ResponseEntity.ok(pageVO)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoryQueryService.getInventory(id)
        return ResponseEntity.ok(
            inventory.toVO(),
        )
    }

    override fun adjustWarningLine(
        id: Long,
        adjustWarningLineRequestVO: AdjustWarningLineRequestVO,
    ): ResponseEntity<Unit> {
        inventoryCommandService.adjustWarningLine(
            id,
            adjustWarningLineRequestVO.safetyStock,
            adjustWarningLineRequestVO.maxStock,
        )
        return ResponseEntity.ok().build()
    }
}
