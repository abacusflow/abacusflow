package org.bruwave.abacusflow.portal.web.user

import org.bruwave.abacusflow.portal.web.api.UserApi
import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.UserVO
import org.bruwave.abacusflow.usecase.user.UserService
import org.bruwave.abacusflow.usecase.user.UserTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class UserController(
    private val userService: UserService,
) : UserApi {
    override fun listUsers(): ResponseEntity<List<BasicUserVO>> {
        val users = userService.listUsers()
        val userVOS =
            users.map { user ->
                BasicUserVO(
                    user.id,
                    user.name,
                    user.nick,
                    user.createdAt.toEpochMilli(),
                )
            }
        return ResponseEntity.ok(userVOS)
    }

    override fun getUser(id: Int): ResponseEntity<UserVO> {
        val user = userService.getUser(id.toLong())
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.nick,
                user.sex,
                user.age,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
            ),
        )
    }

    override fun editUser(
        id: Int,
        userVO: UserVO,
    ): ResponseEntity<UserVO> {
        val user =
            userService.updateUser(
                UserTO(
                    id = id.toLong(),
                    name = userVO.username,
                    nick = userVO.nickname,
                    sex = userVO.sex,
                    age = userVO.age,
                    roles = userVO.roles,
                    enabled = userVO.enabled,
                    locked = userVO.locked,
                    createdAt = Instant.ofEpochMilli(userVO.createdAt),
                    updatedAt = Instant.now(),
                ),
            )
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.nick,
                user.sex,
                user.age,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
            ),
        )
    }
}
