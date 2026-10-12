package com.parkjunhyung.IryeokFitAi.domain.report.service

import com.parkjunhyung.IryeokFitAi.domain.report.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.domain.resume.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.domain.user.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.domain.report.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.report.dto.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.domain.report.service.ReportService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import java.util.*

class ReportServiceTest {

    private lateinit var reportRepository: ReportRepository
    private lateinit var resumeRepository: ResumeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var reportService: ReportService

    @BeforeEach
    fun setup() {
        reportRepository = mockk(relaxed = true)
        resumeRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        reportService = ReportService(reportRepository, resumeRepository, userRepository)
    }

    @Test
    fun `createReport() should create a new report`() {
        val request = CreateReportRequest(
            resumeId = 1L,
            userId = 1L,
            jobPostingUrl = "https://example.com/job",
            title = "New Report"
        )
        val resume = mockk<Resume>()
        val user = mockk<User>()
        val report = mockk<Report>()

        every { resumeRepository.findById(1L) } returns Optional.of(resume)
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { reportRepository.save(any()) } returns report

        val createdReport = reportService.createReport(request)
        assertNotNull(createdReport)
        verify { reportRepository.save(any()) }
    }

    @Test
    fun `createReport() should throw CustomException when resume not found`() {
        val request = CreateReportRequest(
            resumeId = 1L,
            userId = 1L,
            jobPostingUrl = "https://example.com/job",
            title = "New Report"
        )

        every { resumeRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(CustomException::class.java) {
            reportService.createReport(request)
        }
        assertEquals(ErrorCode.RESUME_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getReportById() should return a report`() {
        val report = mockk<Report>()
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val result = reportService.getReportById(1L)

        assertNotNull(result)
    }

    @Test
    fun `getReportById() should throw CustomException when report not found`() {
        every { reportRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(CustomException::class.java) {
            reportService.getReportById(1L)
        }
        assertEquals(ErrorCode.REPORT_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `getReportByUser() should filter out DELETED reports`() {
        val user = mockk<User> {
            every { id } returns 1L
            every { email } returns "test@example.com"
        }
        val activeReport = mockk<Report> {
            every { status } returns ReportStatus.SAVED
        }
        val deletedReport = mockk<Report> {
            every { status } returns ReportStatus.DELETED
        }

        every { userRepository.findByEmail("test@example.com") } returns user
        every { reportRepository.findByUserId(1L) } returns listOf(activeReport, deletedReport)

        val reports = reportService.getReportByUser(1L, "test@example.com")

        assertEquals(1, reports.size)
        assertTrue(reports.all { it.status != ReportStatus.DELETED })
    }

    @Test
    fun `getReportByUser() should throw CustomException when user email not found`() {
        every { userRepository.findByEmail("test@example.com") } returns null

        val exception = assertThrows(CustomException::class.java) {
            reportService.getReportByUser(1L, "test@example.com")
        }
        assertEquals(ErrorCode.ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `getReportByUser() should throw CustomException when userId mismatch`() {
        val user = mockk<User> {
            every { id } returns 2L
            every { email } returns "test@example.com"
        }

        every { userRepository.findByEmail("test@example.com") } returns user

        val exception = assertThrows(CustomException::class.java) {
            reportService.getReportByUser(1L, "test@example.com")
        }
        assertEquals(ErrorCode.ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `updateReportStatus() should update status of a report`() {
        val owner = User(id = 1L, name = "Test", email = "test@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.updateReportStatus(1L, ReportStatus.SAVED, "test@example.com")

        assertEquals(ReportStatus.SAVED, report.status)
        verify { reportRepository.save(report) }
    }

    @Test
    fun `updateReportStatus() should throw CustomException when user email mismatch`() {
        val owner = User(id = 1L, name = "Test", email = "other@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(CustomException::class.java) {
            reportService.updateReportStatus(1L, ReportStatus.SAVED, "test@example.com")
        }
        assertEquals(ErrorCode.ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `deleteReport() should mark the report as DELETED`() {
        val owner = User(id = 1L, name = "Test", email = "test@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.deleteReport(1L, "test@example.com")

        assertEquals(ReportStatus.DELETED, report.status)
        verify { reportRepository.save(report) }
    }

    @Test
    fun `deleteReport() should throw CustomException when user email mismatch`() {
        val owner = User(id = 1L, name = "Test", email = "other@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(CustomException::class.java) {
            reportService.deleteReport(1L, "test@example.com")
        }
        assertEquals(ErrorCode.ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `getReportByIdWithCheck() should return report when user email matches`() {
        val owner = User(id = 1L, name = "Test", email = "test@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val result = reportService.getReportByIdWithCheck(1L, "test@example.com")

        assertNotNull(result)
        assertEquals(report, result)
    }

    @Test
    fun `getReportByIdWithCheck() should throw CustomException when user email mismatch`() {
        val owner = User(id = 1L, name = "Test", email = "other@example.com", password = "test", phoneNumber = "01012345678")
        val report = Report(id = 1L, user = owner, title = "Test", status = ReportStatus.COMPLETED)
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(CustomException::class.java) {
            reportService.getReportByIdWithCheck(1L, "test@example.com")
        }
        assertEquals(ErrorCode.ACCESS_DENIED, exception.errorCode)
    }

    @Test
    fun `getReportByIdWithCheck() should throw CustomException when report not found`() {
        every { reportRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(CustomException::class.java) {
            reportService.getReportByIdWithCheck(1L, "test@example.com")
        }
        assertEquals(ErrorCode.REPORT_NOT_FOUND, exception.errorCode)
    }
}
