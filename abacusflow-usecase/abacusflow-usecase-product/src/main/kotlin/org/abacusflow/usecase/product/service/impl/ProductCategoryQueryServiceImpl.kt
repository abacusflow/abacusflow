package org.abacusflow.usecase.product.service.impl

import org.abacusflow.db.product.ProductCategoryRepository
import org.abacusflow.usecase.product.BasicProductCategoryTO
import org.abacusflow.usecase.product.ProductCategoryTO
import org.abacusflow.usecase.product.mapper.toBasicTO
import org.abacusflow.usecase.product.mapper.toTO
import org.abacusflow.usecase.product.service.ProductCategoryQueryService
import org.springframework.stereotype.Service

@Service
class ProductCategoryQueryServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository,
) : ProductCategoryQueryService {
    override fun getProductCategory(id: Long): ProductCategoryTO =
        productCategoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Product category not found with id: $id") }
            .toTO()

    override fun listBasicProductCategories(): List<BasicProductCategoryTO> = productCategoryRepository.findAll().map { it.toBasicTO() }

    override fun listProductCategories(): List<ProductCategoryTO> {
        return productCategoryRepository.findAll().map { it.toTO() }
    }
}
