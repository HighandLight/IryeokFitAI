package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.access.AccessDeniedException
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
    fun `createReport() should throw IllegalArgumentException when resume not found`() {
        val request = CreateReportRequest(
            resumeId = 1L,
            userId = 1L,
            jobPostingUrl = "https://example.com/job",
            title = "New Report"
        )

        every { resumeRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            reportService.createReport(request)
        }
        assertEquals("Resume 없음: 1", exception.message)
    }

    @Test
    fun `getReportById() should return a report`() {
        val report = mockk<Report>()
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val result = reportService.getReportById(1L)

        assertNotNull(result)
    }

    @Test
    fun `getReportById() should throw IllegalArgumentException when report not found`() {
        every { reportRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            reportService.getReportById(1L)
        }
        assertEquals("report 없음: report_id : 1", exception.message)
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
    fun `getReportByUser() should throw AccessDeniedException when user email not found`() {
        every { userRepository.findByEmail("test@example.com") } returns null

        val exception = assertThrows(AccessDeniedException::class.java) {
            reportService.getReportByUser(1L, "test@example.com")
        }
        assertEquals("사용자 정보를 찾을 수 없습니다.", exception.message)
    }

    @Test
    fun `getReportByUser() should throw AccessDeniedException when userId mismatch`() {
        val user = mockk<User> {
            every { id } returns 2L
            every { email } returns "test@example.com"
        }

        every { userRepository.findByEmail("test@example.com") } returns user

        val exception = assertThrows(AccessDeniedException::class.java) {
            reportService.getReportByUser(1L, "test@example.com")
        }
        assertEquals("다른 사용자의 리포트 목록에는 접근할 수 없습니다.", exception.message)
    }

    @Test
    fun `updateReportStatus() should update status of a report`() {
        val user = mockk<User> {
            every { email } returns "test@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.updateReportStatus(1L, ReportStatus.SAVED, "test@example.com")

        verify { report.status = ReportStatus.SAVED }
        verify { reportRepository.save(report) }
    }

    @Test
    fun `updateReportStatus() should throw AccessDeniedException when user email mismatch`() {
        val user = mockk<User> {
            every { email } returns "other@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(AccessDeniedException::class.java) {
            reportService.updateReportStatus(1L, ReportStatus.SAVED, "test@example.com")
        }
        assertEquals("이 리포트에 접근할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `deleteReport() should mark the report as DELETED`() {
        val user = mockk<User> {
            every { email } returns "test@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.deleteReport(1L, "test@example.com")

        verify { report.status = ReportStatus.DELETED }
        verify { reportRepository.save(report) }
    }

    @Test
    fun `deleteReport() should throw AccessDeniedException when user email mismatch`() {
        val user = mockk<User> {
            every { email } returns "other@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(AccessDeniedException::class.java) {
            reportService.deleteReport(1L, "test@example.com")
        }
        assertEquals("이 리포트에 접근할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `getReportByIdWithCheck() should return report when user email matches`() {
        val user = mockk<User> {
            every { email } returns "test@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val result = reportService.getReportByIdWithCheck(1L, "test@example.com")

        assertNotNull(result)
        assertEquals(report, result)
    }

    @Test
    fun `getReportByIdWithCheck() should throw AccessDeniedException when user email mismatch`() {
        val user = mockk<User> {
            every { email } returns "other@example.com"
        }
        val report = mockk<Report>(relaxed = true) {
            every { user } returns user
        }
        every { reportRepository.findById(1L) } returns Optional.of(report)

        val exception = assertThrows(AccessDeniedException::class.java) {
            reportService.getReportByIdWithCheck(1L, "test@example.com")
        }
        assertEquals("이 리포트에 접근할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `getReportByIdWithCheck() should throw NoSuchElementException when report not found`() {
        every { reportRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows(NoSuchElementException::class.java) {
            reportService.getReportByIdWithCheck(1L, "test@example.com")
        }
        assertEquals("해당 리포트를 찾을 수 없습니다. ID: 1", exception.message)
    }
}
