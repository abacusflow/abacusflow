package org.bruwave.abacusflow.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.domain.AbstractAggregateRoot

@Entity
@Table(name = "permissions")
class Permission(
    val name: String,
    val label: String,
    val description: String,
) : AbstractAggregateRoot<Permission>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
