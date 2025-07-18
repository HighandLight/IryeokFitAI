package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.User

data class CreateReportRequest(
    val resumeId: Long? = null,
    val userId: Long,
    val jobPostingUrl: String? = null,
    val title: String? = null,
    val responsibilities: String? = null,
    val requirements: String? = null,
    val preferred: String? = null,
    val skills: String? = null
) {
    fun toReport(resume: Resume?, user: User): Report {
        return Report(
            resume = resume,
            user = user,
            jobPostingUrl = jobPostingUrl,
            title = title ?: "AI 분석 중...",
            responsibilities = responsibilities,
            requirements = requirements,
            preferred = preferred,
            skills = skills
        )
    }
}
