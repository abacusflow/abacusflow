package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.AdjustWarningLineRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.ListInventoriesPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ProductTypeVO
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
    override fun listInventoriesPage(
        pageIndex: Int,
        pageSize: Int,
        productCategoryId: Long?,
        productName: String?,
        productType: ProductTypeVO?,
        depotName: String?,
    ): ResponseEntity<ListInventoriesPage200ResponseVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page =
            inventoryQueryService.listInventoriesPage(
                pageable,
                productCategoryId,
                productName,
                productType?.name?.uppercase(),
                depotName,
            ).map { it.toBasicVO() }

        val pageVO =
            ListInventoriesPage200ResponseVO(
                content = page.content,
                totalElements = page.totalElements,
                number = page.number,
                propertySize = page.size,
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
