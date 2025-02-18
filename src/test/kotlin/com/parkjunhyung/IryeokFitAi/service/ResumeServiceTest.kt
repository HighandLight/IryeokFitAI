package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.ResumeStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.request.CreateResumeRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResumeServiceTest {
    private val resumeRepository: ResumeRepository = mockk()
    private val userRepository: UserRepository = mockk()

    private val resumeService = ResumeService(resumeRepository, userRepository)

    @Test
    fun `createResume() should create a new resume and return it`() {
        val user = User(id = 1L, name = "박준형", email = "pjh@gmail.com", password = "bestDeveloperEver!")

        val request = CreateResumeRequest(
            userId = 1L,
            originalFilePath = "/uploads/박준형이력서.pdf",
            convertedImagePath = null,
            status = ResumeStatus.UPLOADED
        )

        val expectedResume = Resume(
            id = 1L,
            user = user,
            originalFilePath = request.originalFilePath,
            convertedImagePath = request.convertedImagePath,
            status = request.status
        )

        every { userRepository.findById(1L) } returns java.util.Optional.of(user)
        every { resumeRepository.save(any()) } answers { firstArg() }

        val newResume = resumeService.createResume(request)

        assertEquals(expectedResume.user.id, newResume.user.id)
        assertEquals(expectedResume.originalFilePath, newResume.originalFilePath)
        assertEquals(expectedResume.convertedImagePath, newResume.convertedImagePath)
        assertEquals(expectedResume.status, newResume.status)
    }

    @Test
    fun `createResume() should throw an exception if user does not exist`() {
        val request = CreateResumeRequest(
            userId = 999L,
            originalFilePath = "/uploads/존재하지 않는 이력서.pdf",
            convertedImagePath = null,
            status = ResumeStatus.UPLOADED
        )

        every { userRepository.findById(999L) } returns java.util.Optional.empty()

        val exception = assertFailsWith<IllegalArgumentException> {
            resumeService.createResume(request)
        }

        assertEquals("회원이 존재하지 않습니다! : 999", exception.message)
    }
}
