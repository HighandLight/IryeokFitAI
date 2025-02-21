package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.User

data class CreateReportRequest(
    val resumeId: Long,
    val userId: Long,
    val jobPostingUrl: String,
    val title: String
) {
    fun toReport(resume: Resume, user: User): Report {
        return Report(
            resume = resume,
            user = user,
            jobPostingUrl = jobPostingUrl,
            title = title
        )
    }
}
