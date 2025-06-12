package org.bruwave.abacusflow.warehouse

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.*

@Entity
@Table(name = "warehouses")
class Warehouse(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,
    
    @field:Size(max = 200)
    val location: String? = null,
    
    @field:PositiveOrZero
    val capacity: Int = 0
) : AbstractAggregateRoot<Warehouse>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateWarehouseInfo(newLocation: String?, newCapacity: Int) {
        location = newLocation
        capacity = newCapacity
        updatedAt = Instant.now()
        registerEvent(WarehouseUpdatedEvent(id))
    }
}