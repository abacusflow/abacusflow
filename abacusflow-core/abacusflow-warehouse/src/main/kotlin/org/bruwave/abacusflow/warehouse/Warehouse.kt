package org.bruwave.abacusflow.warehouse

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "warehouses")
class Warehouse(
    name: String,
) : AbstractAggregateRoot<Warehouse>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @field:Size(max = 200)
    var location: String? = null
        private set

    @field:PositiveOrZero
    var capacity: Int = 0
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateWarehouseInfo(newName: String?, newLocation: String? = null, newCapacity: Int? = null) {
        newName?.let {
            name = it
        }
        newLocation?.let {
            location = it
        }
        newCapacity?.let {
            capacity = it
        }
        updatedAt = Instant.now()
        registerEvent(WarehouseUpdatedEvent(id))
    }
}