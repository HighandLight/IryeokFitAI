package com.parkjunhyung.IryeokFitAi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.service.ReportService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@WebMvcTest(controllers = [ReportController::class])
@ExtendWith(SpringExtension::class)
class ReportControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var reportService: ReportService

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    @Test
    fun `createReport() should return created report`() {
        val createRequest = CreateReportRequest(
            resumeId = 1L,
            userId = 1L,
            jobPostingUrl = "https://naver.com",
            title = "테스트 Report"
        )

        val report = Report(
            id = 1L,
            resume = mockk(relaxed = true),
            user = mockk(relaxed = true),
            title = createRequest.title,
            jobPostingUrl = createRequest.jobPostingUrl,
            status = ReportStatus.SAVED,
            createdAt = LocalDateTime.now()
        )

        every { reportService.createReport(any()) } returns report

        mockMvc.perform(
            post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("테스트 Report"))

        verify { reportService.createReport(any()) }
    }

    @Test
    fun `getReportById() should return report if found`() {
        val report = Report(
            id = 1L,
            resume = mockk(relaxed = true),
            user = mockk(relaxed = true),
            title = "테스트 Report",
            jobPostingUrl = "https://naver.com",
            status = ReportStatus.SAVED,
            createdAt = LocalDateTime.now()
        )

        every { reportService.getReportById(1L) } returns report

        mockMvc.perform(
            get("/reports/1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("테스트 Report"))

        verify { reportService.getReportById(1L) }
    }

    @Test
    fun `getReportsByUser() should return reports list`() {
        val reportList = listOf(
            Report(
                id = 1L,
                resume = mockk(relaxed = true),
                user = mockk(relaxed = true),
                title = "1번 리포트",
                jobPostingUrl = "https://naver.com",
                status = ReportStatus.SAVED,
                createdAt = LocalDateTime.now()
            ),
            Report(
                id = 2L,
                resume = mockk(relaxed = true),
                user = mockk(relaxed = true),
                title = "2번 리포트",
                jobPostingUrl = "https://google.com",
                status = ReportStatus.SAVED,
                createdAt = LocalDateTime.now()
            )
        )

        every { reportService.getReportByUser(1L) } returns reportList

        mockMvc.perform(
            get("/reports/user/1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].title").value("1번 리포트"))
            .andExpect(jsonPath("$[1].title").value("2번 리포트"))

        verify { reportService.getReportByUser(1L) }
    }

    @Test
    fun `updateReportStatus() should update report status`() {
        every { reportService.updateReportStatus(1L, ReportStatus.SAVED) } returns Unit

        mockMvc.perform(
            patch("/reports/1/status")
                .param("status", "SAVED")
        )
            .andExpect(status().isNoContent)

        verify { reportService.updateReportStatus(1L, ReportStatus.SAVED) }
    }

    @Test
    fun `deleteReport() should soft delete report`() {
        every { reportService.deleteReport(1L) } returns Unit

        mockMvc.perform(
            delete("/reports/1")
        )
            .andExpect(status().isNoContent)

        verify { reportService.deleteReport(1L) }
    }

    @Test
    fun `getReportById() should return 404 when report not found`() {
        every { reportService.getReportById(1L) } throws IllegalArgumentException("Report not found: 1")

        mockMvc.perform(
            get("/reports/1")
        )
            .andExpect(status().isNotFound)

        verify { reportService.getReportById(1L) }
    }
}
