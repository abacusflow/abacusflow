package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.bruwave.abacusflow.usecase.product.UpdateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.product.mapper.toTO
import org.bruwave.abacusflow.usecase.product.service.ProductCategoryCommandService
import org.bruwave.abacusflow.usecase.product.service.ProductCategoryQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCategoryQueryServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository,
) : ProductCategoryQueryService {
    override fun getProductCategory(id: Long): ProductCategoryTO =
        productCategoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Product category not found with id: $id") }
            .toTO()

    override fun listProductCategories(): List<BasicProductCategoryTO> = productCategoryRepository.findAll().map { it.toBasicTO() }
}
