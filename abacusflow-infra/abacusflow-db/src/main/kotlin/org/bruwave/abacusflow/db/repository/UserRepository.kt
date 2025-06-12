package org.bruwave.abacusflow.db.repository

import org.bruwave.abacusflow.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
    fun existsByName(name: String): Boolean
} 