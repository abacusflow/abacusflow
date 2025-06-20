package org.bruwave.abacusflow.db.product

import org.bruwave.abacusflow.product.ProductInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductInstanceRepository : JpaRepository<ProductInstance, Long>
