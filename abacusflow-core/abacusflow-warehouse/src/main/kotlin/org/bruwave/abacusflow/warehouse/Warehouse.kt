package org.bruwave.abacusflow.warehouse

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "warehouses")
class Warehouse(
    @field:NotBlank(message = "仓库名称不能为空")
    @field:Size(max = 100, message = "仓库名称不能超过100字符")
    val name: String,

    @field:Size(max = 200, message = "位置不能超过200字符")
    val location: String? = null,

    @field:PositiveOrZero(message = "容量不能为负数")
    val capacity: Int = 0
) : AbstractAggregateRoot<Warehouse>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    @OneToMany(mappedBy = "warehouse")
    val inventories: MutableSet<Inventory> = mutableSetOf()

    fun updateWarehouseInfo(newLocation: String?, newCapacity: Int) {
        location = newLocation
        capacity = newCapacity
        updatedAt = Instant.now()
    }
}