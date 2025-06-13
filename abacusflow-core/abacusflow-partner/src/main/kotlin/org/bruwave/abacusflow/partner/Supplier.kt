package org.bruwave.abacusflow.partner

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant


@Entity
@Table(name = "suppliers")
class Supplier(
    name: String,
    phone: String?,
    contactPerson: String?,
) : AbstractAggregateRoot<Supplier>() {
    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:Size(max = 50)
    var contactPerson: String? = contactPerson
        private set

    @field:Pattern(regexp = "^\\d{11}\$")
    var phone: String? = phone
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateContactInfo(newName: String?, newContactPerson: String?, newPhone: String?) {
        newName?.let {
            name = it
        }
        newContactPerson?.let {
            contactPerson = it
        }
        newPhone?.let {
            phone = it
        }
        updatedAt = Instant.now()
        registerEvent(SupplierUpdatedEvent(id))
    }
}

