package org.abacusflow.db.partner

import org.abacusflow.partner.Supplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : JpaRepository<Supplier, Long> {
    fun findByName(name: String): Supplier?

    fun existsByName(name: String): Boolean
}
