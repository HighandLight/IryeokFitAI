package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.request.toUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserServiceTest {
    private val userRepository: UserRepository = mockk()

    private val userService = UserService(userRepository)

    @Test
    fun `createUser() should create a new user and return it`() {
        val createUserRequest = CreateUserRequest(
            name = "Kyle",
            email = "kyle@gmail.com",
            password = "whenInDoubtChooseChange!",
            phoneNumber = "01012345678"
        )

        val expectedUser = createUserRequest.toUser()
        every { userRepository.save(any()) } answers { firstArg() }

        val newUser = userService.createUser(createUserRequest)

        assertEquals(expectedUser.name, newUser.name)
        assertEquals(expectedUser.email, newUser.email)
        assertEquals(expectedUser.phoneNumber, newUser.phoneNumber)
        assertEquals(UserStatus.UNVERIFIED, newUser.status)
    }
}
