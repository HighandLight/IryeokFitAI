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
        val activeReport = mockk<Report> {
            every { status } returns ReportStatus.SAVED
        }
        val deletedReport = mockk<Report> {
            every { status } returns ReportStatus.DELETED
        }

        every { reportRepository.findByUserId(1L) } returns listOf(activeReport, deletedReport)

        val reports = reportService.getReportByUser(1L)

        assertEquals(1, reports.size)
        assertTrue(reports.all { it.status != ReportStatus.DELETED })
    }

    @Test
    fun `updateReportStatus() should update status of a report`() {
        val report = mockk<Report>(relaxed = true)
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.updateReportStatus(1L, ReportStatus.SAVED)

        verify { report.status = ReportStatus.SAVED }
        verify { reportRepository.save(report) }
    }

    @Test
    fun `deleteReport() should mark the report as DELETED`() {
        val report = mockk<Report>(relaxed = true)
        every { reportRepository.findById(1L) } returns Optional.of(report)
        every { reportRepository.save(any()) } returns report

        reportService.deleteReport(1L)

        verify { report.status = ReportStatus.DELETED }
        verify { reportRepository.save(report) }
    }
}
