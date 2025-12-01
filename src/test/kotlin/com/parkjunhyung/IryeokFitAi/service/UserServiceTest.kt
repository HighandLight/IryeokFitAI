package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.request.toUser
import com.parkjunhyung.IryeokFitAi.util.JwtUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertEquals

class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: BCryptPasswordEncoder = mockk(relaxed = true)
    private val jwtUtil: JwtUtil = mockk(relaxed = true)

    private val userService = UserService(userRepository, passwordEncoder, jwtUtil)

    @Test
    fun `createUser() should create a new user and return it`() {
        val createUserRequest = CreateUserRequest(
            name = "Kyle",
            email = "kyle@gmail.com",
            password = "whenInDoubtChooseChange!",
            phoneNumber = "01012345678"
        )

        val hashedPassword = "hashedPassword123"
        every { passwordEncoder.encode(createUserRequest.password) } returns hashedPassword
        every { userRepository.save(any()) } answers { firstArg() }

        val newUser = userService.createUser(createUserRequest)

        assertEquals(createUserRequest.name, newUser.name)
        assertEquals(createUserRequest.email, newUser.email)
        assertEquals(createUserRequest.phoneNumber, newUser.phoneNumber)
        assertEquals(UserStatus.ACTIVATE, newUser.status)
        assertEquals(hashedPassword, newUser.password)
    }
}
