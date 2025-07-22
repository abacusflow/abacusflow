package org.abacusflow.usecase.user.service

import org.abacusflow.usecase.user.BasicUserTO
import org.abacusflow.usecase.user.UserTO

interface UserQueryService {
    fun getUser(id: Long): UserTO?

    fun getUser(name: String): UserTO?

    fun listBasicUsers(): List<BasicUserTO>
}
