package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus

data class UpdateReportStatusRequest(
    val status: ReportStatus
)
