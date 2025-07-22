package org.abacusflow.db.depot

import org.abacusflow.depot.Depot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DepotRepository : JpaRepository<Depot, Long> {
    fun findByName(name: String): Depot?

    fun existsByName(name: String): Boolean
}
