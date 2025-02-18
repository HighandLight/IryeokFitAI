package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserServiceTest {
    private val userRepository: UserRepository = mockk()

    private val userService = UserService(userRepository)

    @Test
    fun `createUser() should create a new user and return it`() {
        val user = User(name = "kyle", email = "kyle@gmail.com", password = "whenInDoubtChooseChange!")
        every { userRepository.save(any()) } answers { firstArg() }

        val newUser = userService.createUser(user)

        assertEquals(user.name, newUser.name)
        assertEquals(user.email, newUser.email)
        assertEquals(user.password, newUser.password)
    }
}
