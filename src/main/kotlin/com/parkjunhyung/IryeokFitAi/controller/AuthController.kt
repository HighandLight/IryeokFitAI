package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.util.JwtUtil
import com.parkjunhyung.IryeokFitAi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,

) {
    @PostMapping("/login")
    fun login(
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<Map<String, String>> {
        val user = userService.findByEmail(email) ?: return ResponseEntity.badRequest()
            .body(mapOf("error" to "이메일 또는 비밀번호가 올바르지 않습니다."))

        return if (passwordEncoder.matches(password, user.password)) {
            val token = jwtUtil.generateToken(user.email)
            ResponseEntity.ok(mapOf("token" to token, "username" to user.name, "email" to user.email, "userId" to user.id.toString()))
        } else {
            ResponseEntity.badRequest().body(mapOf("error" to "이메일 또는 비밀번호가 올바르지 않습니다."))
        }
    }

}