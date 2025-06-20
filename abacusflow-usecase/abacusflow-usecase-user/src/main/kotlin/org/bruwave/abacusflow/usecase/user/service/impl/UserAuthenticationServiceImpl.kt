package org.bruwave.abacusflow.usecase.user.service.impl

import org.bruwave.abacusflow.db.user.UserRepository
import org.bruwave.abacusflow.usecase.user.UserDetailsForLoginTO
import org.bruwave.abacusflow.usecase.user.service.UserAuthenticationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserAuthenticationServiceImpl(
    private val userRepository: UserRepository,
) : UserAuthenticationService {
    override fun getUserForLogin(username: String): UserDetailsForLoginTO {
        val user =
            userRepository.findByName(username)
                ?: throw NoSuchElementException("User not found")

        return UserDetailsForLoginTO(
            id = user.id,
            name = user.name,
            nick = user.nick,
            password = user.password,
            roleNames = user.roles.map { it.name },
            enabled = user.enabled,
            locked = user.locked,
        )
    }

    override fun validateCredentials(
        username: String,
        password: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateLastLoginTime(userId: Long) {
        TODO("Not yet implemented")
    }
}
