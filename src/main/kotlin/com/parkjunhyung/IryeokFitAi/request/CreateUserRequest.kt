package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.User

data class CreateUserRequest (
    val name: String,
    val email: String,
    val password: String,
)

fun CreateUserRequest.toUser() = User(name = name, email = email, password = password)
