package com.parkjunhyung.IryeokFitAi.domain.feedback.repository

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackRepository : JpaRepository<Feedback, Long>{
    fun findByReportId(reportId: Long): List<Feedback>
}