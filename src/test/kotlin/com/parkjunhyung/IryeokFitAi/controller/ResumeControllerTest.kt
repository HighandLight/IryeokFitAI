package com.parkjunhyung.IryeokFitAi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ResumeStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.request.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.service.ResumeService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(value = [ResumeController::class])
@ExtendWith(MockKExtension::class)
class ResumeControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var resumeService: ResumeService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `createResume() should create and return the resume`() {
        val createResumeRequest = CreateResumeRequest(
            userId = 1L,
            originalFilePath = "/uploads/박준형이력서.pdf",
            convertedImagePath = null,
            status = ResumeStatus.UPLOADED
        )

        val jsonString = objectMapper.writeValueAsString(createResumeRequest)

        val mockResume = Resume(
            id = 1L,
            user = User(id = 1L, name = "박준형", email = "parkjunhyung@gmail.com", phoneNumber = "01012345678",password = "bestSoftwareEngineer!"),
            originalFilePath = "/uploads/박준형이력서.pdf",
            convertedImagePath = null,
            status = ResumeStatus.UPLOADED
        )

        every { resumeService.createResume(any()) } returns mockResume

        mockMvc
            .perform(
                post("/resumes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(mockResume.id))
            .andExpect(jsonPath("$.userId").value(mockResume.user.id))
            .andExpect(jsonPath("$.originalFilePath").value(mockResume.originalFilePath))
            .andExpect(jsonPath("$.convertedImagePath").isEmpty)
            .andExpect(jsonPath("$.status").value(mockResume.status.toString()))
    }

    @Test
    fun `deleteResume() should return 204 No Content`() {
        every { resumeService.deleteResume(1L) } just runs

        mockMvc.perform(delete("/resumes/1"))
            .andExpect(status().isNoContent())
    }

    @Test
    fun `deleteResume() should return 404 if resume not found`() {
        every { resumeService.deleteResume(any()) } throws IllegalArgumentException("이력서를 찾을 수 없습니다")

        mockMvc.perform(delete("/resumes/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("이력서를 찾을 수 없습니다"))
    }


}
