package org.bruwave.abacusflow.db.partner

import org.bruwave.abacusflow.partner.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByName(name: String): Customer?
    fun existsByName(name: String): Boolean
}