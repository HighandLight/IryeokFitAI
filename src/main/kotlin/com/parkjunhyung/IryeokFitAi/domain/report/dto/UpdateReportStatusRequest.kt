package com.parkjunhyung.IryeokFitAi.domain.report.dto

import com.parkjunhyung.IryeokFitAi.domain.report.entity.ENUM.ReportStatus

data class UpdateReportStatusRequest(
    val status: ReportStatus
)
