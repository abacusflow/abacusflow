package org.bruwave.abacusflow.portal.web

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
        return super.listProducts()
    }

    override fun getProduct(id: Long): ResponseEntity<ProductVO> {
        return super.getProduct(id)
    }

    override fun addProduct(createProductInputVO: CreateProductInputVO): ResponseEntity<ProductVO> {
        return super.addProduct(createProductInputVO)
    }

    override fun updateProduct(id: Long, updateProductInputVO: UpdateProductInputVO): ResponseEntity<ProductVO> {
        return super.updateProduct(id, updateProductInputVO)
    }

    override fun deleteProduct(id: Long): ResponseEntity<ProductVO> {
        return super.deleteProduct(id)
    }
} 