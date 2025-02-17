package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import org.springframework.stereotype.Service

@Service
class ReportService {
    fun createReport(newReport: Report): Report {
        return newReport
    }
}
