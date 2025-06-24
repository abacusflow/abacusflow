package org.bruwave.abacusflow.portal.web.product

import org.apache.tomcat.jni.Buffer.address
import org.bruwave.abacusflow.portal.web.api.ProductsApi
import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.CreateProductInputVO
import org.bruwave.abacusflow.portal.web.model.ListProductsPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ListSuppliersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ProductTypeVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.portal.web.model.UpdateProductInputVO
import org.bruwave.abacusflow.portal.web.partner.toBasicVO
import org.bruwave.abacusflow.portal.web.product.mapper.toBasicVO
import org.bruwave.abacusflow.portal.web.product.mapper.toVO
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO
import org.bruwave.abacusflow.usecase.product.service.ProductCommandService
import org.bruwave.abacusflow.usecase.product.service.ProductQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productCommandService: ProductCommandService,
    private val productQueryService: ProductQueryService,
) : ProductsApi {
    override fun listProductsPage(
        pageIndex: Int,
        pageSize: Int,
        name: String?,
        type: ProductTypeVO?,
        enabled: Boolean?,
        categoryId: Long?
    ): ResponseEntity<ListProductsPage200ResponseVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page = productQueryService.listProductsPage(
            pageable,
            name = name,
            type = type?.name?.uppercase(),
            enabled = enabled,
            categoryId = categoryId,
        ).map { it.toBasicVO() }

        val pageVO = ListProductsPage200ResponseVO(
            content = page.content,
            totalElements = page.totalElements,
            number = page.number,
            propertySize = page.size
        )

        return ResponseEntity.ok(pageVO)
    }

    override fun getProduct(id: Long): ResponseEntity<ProductVO> {
        val product = productQueryService.getProduct(id)
        return ResponseEntity.ok(
            product.toVO(),
        )
    }

    override fun addProduct(createProductInputVO: CreateProductInputVO): ResponseEntity<ProductVO> {
        val product =
            productCommandService.createProduct(
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
            productCommandService.updateProduct(
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
        productCommandService.deleteProduct(id)
        return ResponseEntity.ok().build()
    }
}
