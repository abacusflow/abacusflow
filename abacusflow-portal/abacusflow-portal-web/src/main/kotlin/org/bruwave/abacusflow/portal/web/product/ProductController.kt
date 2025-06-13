package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.api.ProductsApi
import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.CreateProductInputVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.portal.web.model.UpdateProductInputVO
import org.bruwave.abacusflow.usecase.product.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
): ProductsApi {

    override fun listProducts(): ResponseEntity<List<BasicProductVO>> {
        val products = productService.listProducts()
        val productVOs = products.map { product ->
            BasicProductVO(
                product.id,
                product.name,
                product.categoryName,
                product.supplierId,
                product.unitPrice,
                product.specification
            )
        }
        return ResponseEntity.ok(productVOs)
    }

    override fun getProduct(id: Long): ResponseEntity<ProductVO> {
        val product = productService.getProduct(id)
        return ResponseEntity.ok(
            ProductVO(
                product.id,
                product.name,
                product.categoryName,
                product.supplierId,
                product.unitPrice,
                product.specification,
                product.createdAt.toEpochMilli(),
                product.updatedAt.toEpochMilli()
            )
        )
    }

    override fun addProduct(createProductInputVO: CreateProductInputVO): ResponseEntity<ProductVO> {
        val product = productService.createProduct(
            CreateProductInputTO(
                createProductInputVO.name,
                createProductInputVO.categoryName,
                createProductInputVO.supplierId,
                createProductInputVO.unitPrice,
                createProductInputVO.specification
            )
        )
        return ResponseEntity.ok(
            ProductVO(
                product.id,
                product.name,
                product.categoryName,
                product.supplierId,
                product.unitPrice,
                product.specification,
                product.createdAt.toEpochMilli(),
                product.updatedAt.toEpochMilli()
            )
        )
    }

    override fun updateProduct(
        id: Long,
        updateProductInputVO: UpdateProductInputVO
    ): ResponseEntity<ProductVO> {
        val product = productService.updateProduct(
            id,
            UpdateProductInputTO(
                name = updateProductInputVO.name,
                categoryName = updateProductInputVO.categoryName,
                supplierId = updateProductInputVO.supplierId,
                unitPrice = updateProductInputVO.unitPrice,
                specification = updateProductInputVO.specification
            )
        )
        return ResponseEntity.ok(
            ProductVO(
                product.id,
                product.name,
                product.categoryName,
                product.supplierId,
                product.unitPrice,
                product.specification,
                product.createdAt.toEpochMilli(),
                product.updatedAt.toEpochMilli()
            )
        )
    }

    override fun deleteProduct(id: Long): ResponseEntity<ProductVO> {
        productService.deleteProduct(id)
        return ResponseEntity.ok().build()
    }
} 