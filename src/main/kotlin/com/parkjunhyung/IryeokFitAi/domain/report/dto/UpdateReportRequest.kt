package com.parkjunhyung.IryeokFitAi.domain.report.dto

import com.parkjunhyung.IryeokFitAi.domain.report.entity.ENUM.ReportStatus

data class UpdateReportRequest(
    var resumeId: Long? = null,
    var title: String? = null,
    var jobPostingUrl: String? = null,
    var responsibilities: String? = null,
    var requirements: String? = null,
    var preferred: String? = null,
    var skills: String? = null,
    var status: ReportStatus? = null

)
