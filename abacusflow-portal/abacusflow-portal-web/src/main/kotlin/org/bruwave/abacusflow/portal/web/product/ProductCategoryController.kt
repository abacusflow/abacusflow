package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.api.ProductCategoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicProductCategoryVO
import org.bruwave.abacusflow.portal.web.model.CreateProductCategoryInputVO
import org.bruwave.abacusflow.portal.web.model.ProductCategoryVO
import org.bruwave.abacusflow.portal.web.model.UpdateProductCategoryInputVO
import org.bruwave.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.ProductCategoriesService
import org.bruwave.abacusflow.usecase.product.UpdateProductCategoryInputTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductCategoryController(
    private val productCategoriesService: ProductCategoriesService
) : ProductCategoriesApi {

    override fun listProductCategories(): ResponseEntity<List<BasicProductCategoryVO>> {
        val categories = productCategoriesService.listProductCategories()
        val categoryVOs = categories.map { category ->
            category.toVO()
        }
        return ResponseEntity.ok(categoryVOs)
    }

    override fun getProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        val category = productCategoriesService.getProductCategory(id)
        return ResponseEntity.ok(
            category.toVO()
        )
    }

    override fun addProductCategory(createProductCategoryInputVO: CreateProductCategoryInputVO): ResponseEntity<ProductCategoryVO> {
        val category = productCategoriesService.createProductCategory(
            CreateProductCategoryInputTO(
                name = createProductCategoryInputVO.name,
                parentId = createProductCategoryInputVO.parentId,
                description = createProductCategoryInputVO.description
            )
        )
        return ResponseEntity.ok(
            category.toVO()
        )
    }

    override fun updateProductCategory(
        id: Long,
        updateProductCategoryInputVO: UpdateProductCategoryInputVO
    ): ResponseEntity<ProductCategoryVO> {
        val category = productCategoriesService.updateProductCategory(
            id,
            UpdateProductCategoryInputTO(
                name = updateProductCategoryInputVO.name,
                parentId = updateProductCategoryInputVO.parentId,
                description = updateProductCategoryInputVO.description
            )
        )
        return ResponseEntity.ok(
            category.toVO()
        )
    }

    override fun deleteProductCategory(id: Long): ResponseEntity<Unit> {
        productCategoriesService.deleteProductCategory(id)
        return ResponseEntity.ok().build()
    }
}