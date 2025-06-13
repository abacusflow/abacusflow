package org.bruwave.abacusflow.usecase.product.impl

import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCategoryServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository
) : ProductCategoryService {
    override fun createProductCategory(input: CreateProductCategoryInputTO): ProductCategoryTO {
        val category = ProductCategory(
            name = input.name,
            code = input.code,
            description = input.description
        )
        return productCategoryRepository.save(category).toProductCategoryTO()
    }

    override fun updateProductCategory(id: Long, input: UpdateProductCategoryInputTO): ProductCategoryTO {
        val category = productCategoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product category not found with id: $id") }

        category.apply {
            name = input.name
            code = input.code
            description = input.description
        }

        return productCategoryRepository.save(category).toProductCategoryTO()
    }

    override fun deleteProductCategory(id: Long): ProductCategoryTO {
        val category = productCategoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product category not found with id: $id") }

        productCategoryRepository.delete(category)
        return category.toProductCategoryTO()
    }

    override fun getProductCategory(id: Long): ProductCategoryTO {
        return productCategoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product category not found with id: $id") }
            .toProductCategoryTO()
    }

    override fun listProductCategories(): List<BasicProductCategoryTO> {
        return productCategoryRepository.findAll().map { it.toBasicProductCategoryTO() }
    }
}

private fun ProductCategory.toProductCategoryTO() = ProductCategoryTO(
    id = id,
    name = name,
    code = code,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun ProductCategory.toBasicProductCategoryTO() = BasicProductCategoryTO(
    id = id,
    name = name,
    code = code
)