package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.api.ProductInstanceApi
import org.bruwave.abacusflow.portal.web.model.BasicProductInstanceVO
import org.bruwave.abacusflow.portal.web.product.mapper.toVO
import org.bruwave.abacusflow.usecase.product.service.ProductInstanceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductInstanceController(
    private val productInstanceService: ProductInstanceService,
) : ProductInstanceApi {
    override fun listProductInstances(): ResponseEntity<List<BasicProductInstanceVO>> {
        val productVOs =
            productInstanceService.listProductInstances().map { product ->
                product.toVO()
            }
        return ResponseEntity.ok(productVOs)
    }
}
