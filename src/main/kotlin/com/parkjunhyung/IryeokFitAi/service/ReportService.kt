package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import org.springframework.stereotype.Service

@Service
class ReportService(
    val reportRepository: ReportRepository
) {
    fun createReport(newReport: Report): Report {
        return reportRepository.save(newReport)
    }
}
