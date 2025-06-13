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
): ProductCategoriesApi {

    override fun listProductCategories(): ResponseEntity<List<BasicProductCategoryVO>> {
        val categories = productCategoryService.listProductCategories()
        val categoryVOs = categories.map { category ->
            BasicProductCategoryVO(
                category.id,
                category.name,
                category.code
            )
        }
        return ResponseEntity.ok(categoryVOs)
    }

    override fun getProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        val category = productCategoryService.getProductCategory(id)
        return ResponseEntity.ok(
            ProductCategoryVO(
                category.id,
                category.name,
                category.code,
                category.createdAt.toEpochMilli(),
                category.updatedAt.toEpochMilli()
            )
        )
    }

    override fun addProductCategory(createProductCategoryInputVO: CreateProductCategoryInputVO): ResponseEntity<ProductCategoryVO> {
        val category = productCategoryService.createProductCategory(
            CreateProductCategoryInputTO(
                createProductCategoryInputVO.name,
                createProductCategoryInputVO.code
            )
        )
        return ResponseEntity.ok(
            ProductCategoryVO(
                category.id,
                category.name,
                category.code,
                category.createdAt.toEpochMilli(),
                category.updatedAt.toEpochMilli()
            )
        )
    }

    override fun updateProductCategory(
        id: Long,
        updateProductCategoryInputVO: UpdateProductCategoryInputVO
    ): ResponseEntity<ProductCategoryVO> {
        val category = productCategoryService.updateProductCategory(
            id,
            UpdateProductCategoryInputTO(
                name = updateProductCategoryInputVO.name,
                code = updateProductCategoryInputVO.code
            )
        )
        return ResponseEntity.ok(
            ProductCategoryVO(
                category.id,
                category.name,
                category.code,
                category.createdAt.toEpochMilli(),
                category.updatedAt.toEpochMilli()
            )
        )
    }

    override fun deleteProductCategory(id: Long): ResponseEntity<ProductCategoryVO> {
        productCategoryService.deleteProductCategory(id)
        return ResponseEntity.ok().build()
    }
}