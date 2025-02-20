package com.parkjunhyung.IryeokFitAi.repository

import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackRepository : JpaRepository<Feedback, Long>{
    fun findByReportId(reportId: Long): List<Feedback>
}