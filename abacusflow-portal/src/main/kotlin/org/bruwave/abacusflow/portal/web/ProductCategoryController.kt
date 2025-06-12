package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.product.ProductCategoryService
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/product-categories")
class ProductCategoryController(private val productCategoryService: ProductCategoryService) {

    @GetMapping
    fun listProductCategories(): List<ProductCategoryTO> = productCategoryService.listProductCategories()

    @PostMapping
    fun createProductCategory(@RequestBody category: ProductCategoryTO): ProductCategoryTO = productCategoryService.createProductCategory(category)

    @GetMapping("/{id}")
    fun getProductCategory(@PathVariable id: Long): ProductCategoryTO = productCategoryService.getProductCategory(id)

    @PutMapping("/{id}")
    fun updateProductCategory(@PathVariable id: Long, @RequestBody categoryTO: ProductCategoryTO): ProductCategoryTO {
        return productCategoryService.updateProductCategory(categoryTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteProductCategory(@PathVariable id: Long): ProductCategoryTO {
        return productCategoryService.deleteProductCategory(ProductCategoryTO(id = id, name = "", code = "", createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 