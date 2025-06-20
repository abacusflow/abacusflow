package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO

interface ProductInstanceService {
    fun listProductInstances(): List<BasicProductInstanceTO>
}