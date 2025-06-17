package org.bruwave.abacusflow.db.user

import org.bruwave.abacusflow.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    override fun findAll(): List<User>

    fun findByName(name: String): User?

    fun existsByName(name: String): Boolean
}
