package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.bruwave.abacusflow.usecase.product.UpdateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.mapper.toTO
import org.bruwave.abacusflow.usecase.product.service.ProductCategoryCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCategoryCommandServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository,
) : ProductCategoryCommandService {
    override fun createProductCategory(input: CreateProductCategoryInputTO): ProductCategoryTO {
        val parentCategoryFromInput =
            productCategoryRepository
                .findById(input.parentId)
                .orElseThrow { NoSuchElementException("Product category not found with id: ${input.parentId}") }

        val category =
            ProductCategory(
                name = input.name,
                parent = parentCategoryFromInput,
                description = input.description,
            )
        return productCategoryRepository.save(category).toTO()
    }

    override fun updateProductCategory(
        id: Long,
        input: UpdateProductCategoryInputTO,
    ): ProductCategoryTO {
        val category =
            productCategoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Product category not found with id: $id") }

        category.apply {
            updateBasicInfo(
                input.name,
                input.description,
            )

            input.parentId?.let { parentId ->
                val parentCategoryFromInput =
                    productCategoryRepository
                        .findById(parentId)
                        .orElseThrow { NoSuchElementException("Product category not found with id: $parentId") }

                changeParent(parentCategoryFromInput)
            }
        }

        return productCategoryRepository.save(category).toTO()
    }

    override fun deleteProductCategory(id: Long): ProductCategoryTO {
        val category =
            productCategoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Product category not found with id: $id") }

        productCategoryRepository.delete(category)
        return category.toTO()
    }
}
