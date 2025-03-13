package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.util.JwtUtil
import com.parkjunhyung.IryeokFitAi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    @PostMapping("/login")
    fun login(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        val email = request["email"]
        val password = request["password"]

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "이메일 또는 비밀번호가 없습니다."))
        }

        val user = userService.findByEmail(email)
        if (user == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "이메일 또는 비밀번호가 올바르지 않습니다."))
        }

        if (!passwordEncoder.matches(password, user.password)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "이메일 또는 비밀번호가 올바르지 않습니다."))
        }

        val role = user.role.name ?: "USER"
        val token = jwtUtil.generateToken(user.email, role)


        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "username" to user.name,
                "userId" to user.id.toString()
            )
        )
    }
}
