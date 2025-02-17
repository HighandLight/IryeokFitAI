package com.parkjunhyung.IryeokFitAi.repository

import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository: JpaRepository<Report, Long>
