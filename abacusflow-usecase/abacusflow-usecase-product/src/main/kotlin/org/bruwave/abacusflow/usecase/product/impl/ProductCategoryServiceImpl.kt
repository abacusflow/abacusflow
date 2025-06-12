package org.bruwave.abacusflow.usecase.product.impl

import org.bruwave.abacusflow.db.repository.ProductCategoryRepository
import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.ProductCategoryService
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCategoryServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository,
) : ProductCategoryService {
    override fun createCategory(category: ProductCategoryTO): ProductCategoryTO {
        val newCategory = ProductCategory(
            name = category.name,
            code = category.code
        )
        return productCategoryRepository.save(newCategory).toProductCategoryTO()
    }

    override fun updateCategory(categoryTO: ProductCategoryTO): ProductCategoryTO {
        val category = productCategoryRepository.findById(categoryTO.id)
            .orElseThrow { NoSuchElementException("Product category not found") }
        category.name = categoryTO.name
        category.code = categoryTO.code
        return productCategoryRepository.save(category).toProductCategoryTO()
    }

    override fun deleteCategory(categoryTO: ProductCategoryTO): ProductCategoryTO {
        val category = productCategoryRepository.findById(categoryTO.id)
            .orElseThrow { NoSuchElementException("Product category not found") }
        productCategoryRepository.delete(category)
        return categoryTO
    }

    override fun getCategory(id: Long): ProductCategoryTO {
        return productCategoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product category not found") }
            .toProductCategoryTO()
    }

    override fun getCategory(name: String): ProductCategoryTO {
        return productCategoryRepository.findByName(name)
            ?.toProductCategoryTO()
            ?: throw NoSuchElementException("Product category not found")
    }

    override fun getCategoryByCode(code: String): ProductCategoryTO {
        return productCategoryRepository.findByCode(code)
            ?.toProductCategoryTO()
            ?: throw NoSuchElementException("Product category not found")
    }

    override fun listCategories(): List<ProductCategoryTO> {
        return productCategoryRepository.findAll().map { it.toProductCategoryTO() }
    }

    private fun ProductCategory.toProductCategoryTO() = ProductCategoryTO(
        id = id,
        name = name,
        code = code
    )
} 