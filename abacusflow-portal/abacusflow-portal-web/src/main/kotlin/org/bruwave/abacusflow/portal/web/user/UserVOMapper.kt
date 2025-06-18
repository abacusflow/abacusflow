package org.bruwave.abacusflow.portal.web.user

import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.SexVO
import org.bruwave.abacusflow.portal.web.model.SexVO.female
import org.bruwave.abacusflow.portal.web.model.SexVO.male
import org.bruwave.abacusflow.portal.web.model.UserVO
import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.UserTO

fun UserTO.toVO(): UserVO =
    UserVO(
        id = id,
        name = name,
        age = age,
        nick = nick,
        roleIds = roleIds,
        enabled = enabled,
        locked = locked,
        createdAt= createdAt.toEpochMilli(),
        updatedAt= updatedAt.toEpochMilli(),
        sex= sex?.let { mapSexTOToVO(it) },
    )

fun BasicUserTO.toBasicVO(): BasicUserVO =
    BasicUserVO(
        id,
        name,
        nick,
        enabled,
        locked,
    )

fun SexVO.toTO(): String =
    when (this) {
        male -> "M"
        female -> "F"
    }

private fun mapSexTOToVO(sex: String): SexVO =
    when (sex.uppercase()) {
        "M" -> SexVO.male
        "F" -> SexVO.female
        else -> throw NoSuchElementException("SexVO $sex not found")
    }
