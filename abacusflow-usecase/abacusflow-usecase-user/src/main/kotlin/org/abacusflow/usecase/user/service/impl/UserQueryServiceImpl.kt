package org.abacusflow.usecase.user.service.impl

import org.abacusflow.db.user.UserRepository
import org.abacusflow.usecase.user.BasicUserTO
import org.abacusflow.usecase.user.UserTO
import org.abacusflow.usecase.user.mapper.toBasicTO
import org.abacusflow.usecase.user.mapper.toTO
import org.abacusflow.usecase.user.service.UserQueryService
import org.abacusflow.user.UserPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserQueryServiceImpl(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
) : UserQueryService {
    override fun getUser(id: Long): UserTO? {
        val user =
            userRepository.findById(id)
                .orElseThrow { NoSuchElementException("User not found") }

        return user.toTO()
    }

    override fun getUser(name: String): UserTO? =
        userRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("User not found")

    override fun listBasicUsers(): List<BasicUserTO> = userRepository.findAll().map { it.toBasicTO() }
}
