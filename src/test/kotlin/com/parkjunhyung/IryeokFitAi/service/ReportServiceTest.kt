package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import io.awspring.cloud.s3.S3Template
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReportServiceTest {
    private val reportRepository: ReportRepository = mockk()
    private val s3Template: S3Template = mockk()  // Mock S3Template
    private val bucketName = "iryeokfit"

    private val reportService = ReportService(reportRepository, s3Template, bucketName)

    @Test
    fun `createReport() should create a new report and return it`() {
        val report = Report(name = "Luke")
        every { reportRepository.save(any()) } answers { firstArg() }

        val newReport = reportService.createReport(report)

        assertEquals(report.name, newReport.name)
    }
}
