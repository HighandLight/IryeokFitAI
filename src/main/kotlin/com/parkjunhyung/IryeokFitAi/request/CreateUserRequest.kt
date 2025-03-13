package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus

data class CreateUserRequest (
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)

fun CreateUserRequest.toUser() = User(
    name = name,
    email = email,
    password = password,
    phoneNumber = phoneNumber,
    status = UserStatus.ACTIVATE, // TODO : 인증 구현 후 UNVERIFIED 가 default.
)
