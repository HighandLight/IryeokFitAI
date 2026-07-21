package com.parkjunhyung.IryeokFitAi.domain.report.repository

import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository: JpaRepository<Report, Long> {
    fun findByUserId(userId: Long): List<Report>
}
