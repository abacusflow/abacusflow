package org.bruwave.abacusflow.db.repository

import org.bruwave.abacusflow.warehouse.Warehouse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository : JpaRepository<Warehouse, Long> {
    fun findByName(name: String): Warehouse?
    fun existsByName(name: String): Boolean
} 