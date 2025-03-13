package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.UserDto
import com.parkjunhyung.IryeokFitAi.dto.toUserDto
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.request.SigninRequest
import com.parkjunhyung.IryeokFitAi.request.toUser
import com.parkjunhyung.IryeokFitAi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController (
    private val userService: UserService
){
    @PostMapping
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): UserDto {
        val user = userService.createUser(createUserRequest)
        return user.toUserDto()
    }

    @PostMapping("/login")
    fun login(@RequestBody signinRequest: SigninRequest): ResponseEntity<Any> {
        val token = userService.authenticateUser(signinRequest)
        return if (token != null) {
            ResponseEntity.ok(mapOf("token" to token))
        } else {
            ResponseEntity.status(401).body(mapOf("message" to "로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다."))
        }
    }
}