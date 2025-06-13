package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.portal.web.api.ProductsApi
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.usecase.product.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
): ProductsApi {
    override fun listProducts(): ResponseEntity<List<ProductVO>> {
        return super.listProducts()
    }

    override fun getProduct(id: Long): ResponseEntity<ProductVO> {
        return super.getProduct(id)
    }

    override fun addProduct(productVO: ProductVO): ResponseEntity<ProductVO> {
        return super.addProduct(productVO)
    }

    override fun updateProduct(id: Long, productVO: ProductVO): ResponseEntity<ProductVO> {
        return super.updateProduct(id, productVO)
    }

    override fun deleteProduct(id: Long): ResponseEntity<ProductVO> {
        return super.deleteProduct(id)
    }
} 