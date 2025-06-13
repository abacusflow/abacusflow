package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.portal.web.api.ProductCategoriesApi
import org.bruwave.abacusflow.portal.web.model.ProductCategoryVO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PurchaseOrderController(
    private val purchaseOrderService: PurchaseOrderService
): ProductCategoriesApi {
    override fun listProductCategories(): ResponseEntity<List<ProductCategoryVO>> {
        return super.listProductCategories()
    }

    override fun getProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        return super.getProductCategory(id)
    }

    override fun addProductCategory(productCategoryVO: ProductCategoryVO): ResponseEntity<ProductCategoryVO> {
        return super.addProductCategory(productCategoryVO)
    }

    override fun updateProductCategory(
        id: Long,
        productCategoryVO: ProductCategoryVO
    ): ResponseEntity<ProductCategoryVO> {
        return super.updateProductCategory(id, productCategoryVO)
    }

    override fun deleteProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        return super.deleteProductCategory(id)
    }
}