package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReportServiceTest {

    private val reportRepository: ReportRepository = mockk()

    private val reportService = ReportService(reportRepository)

    @Test
    fun `createReport() should create a new report and return it`() {
        val report = Report(name = "Kyle")
        every { reportRepository.save(any()) } answers { firstArg() }

        val newReport = reportService.createReport(report)

        val expected = report.name

        assertEquals(expected, newReport.name)
    }
}