package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.product.ProductService
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun listProducts(): List<ProductTO> = productService.listProducts()

    @PostMapping
    fun createProduct(@RequestBody product: ProductTO): ProductTO = productService.createProduct(product)

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ProductTO = productService.getProduct(id)

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody productTO: ProductTO): ProductTO {
        return productService.updateProduct(productTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ProductTO {
        return productService.deleteProduct(ProductTO(
            id = id,
            name = "",
            categoryName = "",
            supplierId = 0,
            unitPrice = 0.0,
            specification = "",
            createdAt = java.time.Instant.now(),
            updatedAt = java.time.Instant.now()
        ))
    }
} 