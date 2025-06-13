package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.api.ProductCategoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicProductCategoryVO
import org.bruwave.abacusflow.portal.web.model.CreateProductCategoryInputVO
import org.bruwave.abacusflow.portal.web.model.ProductCategoryVO
import org.bruwave.abacusflow.portal.web.model.UpdateProductCategoryInputVO
import org.bruwave.abacusflow.usecase.product.ProductCategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductCategoryController(
    private val productCategoryService: ProductCategoryService
) : ProductCategoriesApi {

    override fun listProductCategories(): ResponseEntity<List<BasicProductCategoryVO>> {
        return super.listProductCategories()
    }

    override fun getProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        return super.getProductCategory(id)
    }

    override fun addProductCategory(createProductCategoryInputVO: CreateProductCategoryInputVO): ResponseEntity<ProductCategoryVO> {
        return super.addProductCategory(createProductCategoryInputVO)
    }

    override fun updateProductCategory(
        id: Long,
        updateProductCategoryInputVO: UpdateProductCategoryInputVO
    ): ResponseEntity<ProductCategoryVO> {
        return super.updateProductCategory(id, updateProductCategoryInputVO)
    }

    override fun deleteProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        return super.deleteProductCategory(id)
    }
}