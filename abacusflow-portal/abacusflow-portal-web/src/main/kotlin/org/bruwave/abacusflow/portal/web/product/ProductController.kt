package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.api.ProductsApi
import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.CreateProductInputVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.portal.web.model.UpdateProductInputVO
import org.bruwave.abacusflow.portal.web.product.mapper.toVO
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO
import org.bruwave.abacusflow.usecase.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService,
) : ProductsApi {
    override fun listProducts(
        categoryId: Long?,
        name: String?,
    ): ResponseEntity<List<BasicProductVO>> {
        val productVOs =
            productService.listProducts(categoryId).map { product ->
                product.toVO()
            }
        return ResponseEntity.ok(productVOs)
    }

    override fun getProduct(id: Long): ResponseEntity<ProductVO> {
        val product = productService.getProduct(id)
        return ResponseEntity.ok(
            product.toVO(),
        )
    }

    override fun addProduct(createProductInputVO: CreateProductInputVO): ResponseEntity<ProductVO> {
        val product =
            productService.createProduct(
                CreateProductInputTO(
                    name = createProductInputVO.name,
                    categoryId = createProductInputVO.categoryId,
                    unit = createProductInputVO.unit.name,
                    note = createProductInputVO.note,
                    type = createProductInputVO.type.name,
                    specification = createProductInputVO.specification,
                ),
            )
        return ResponseEntity.ok(
            product.toVO(),
        )
    }

    override fun updateProduct(
        id: Long,
        updateProductInputVO: UpdateProductInputVO,
    ): ResponseEntity<ProductVO> {
        val product =
            productService.updateProduct(
                id,
                UpdateProductInputTO(
                    name = updateProductInputVO.name,
                    categoryId = updateProductInputVO.categoryId,
                    unit = updateProductInputVO.unit?.name,
                    note = updateProductInputVO.note,
                    type = updateProductInputVO.type?.name,
                    specification = updateProductInputVO.specification,
                ),
            )
        return ResponseEntity.ok(
            product.toVO(),
        )
    }

    override fun deleteProduct(id: Long): ResponseEntity<Unit> {
        productService.deleteProduct(id)
        return ResponseEntity.ok().build()
    }
}
