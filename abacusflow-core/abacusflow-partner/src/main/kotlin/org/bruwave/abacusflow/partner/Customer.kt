package org.bruwave.abacusflow.partner

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.*

@Entity
@Table(name = "customers")
class Customer(
    name: String,
    phone: String?,
    ) : AbstractAggregateRoot<Customer>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @field:Pattern(regexp = "^\\d{11}\$")
    var phone: String? = phone
        private set

    @field:Size(max = 200)
    var address: String? = null
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateContactInfo(newName: String?, newAddress: String?, newPhone: String?) {
        newName?.let {
            name = it
        }
        newAddress?.let {
            address = it
        }
        newPhone?.let {
            phone = it
        }
        updatedAt = Instant.now()
        registerEvent(CustomerUpdatedEvent(id))
    }
}