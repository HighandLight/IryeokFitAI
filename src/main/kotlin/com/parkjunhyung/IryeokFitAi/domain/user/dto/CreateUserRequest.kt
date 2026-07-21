package com.parkjunhyung.IryeokFitAi.domain.user.dto

import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserRole
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserStatus

data class CreateUserRequest (
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val role: UserRole = UserRole.USER
)

fun CreateUserRequest.toUser() = User(
    name = name,
    email = email,
    password = password,
    phoneNumber = phoneNumber,
    status = UserStatus.ACTIVATE, // TODO : 인증 구현 후 UNVERIFIED가 기본 default
    role = role

)
