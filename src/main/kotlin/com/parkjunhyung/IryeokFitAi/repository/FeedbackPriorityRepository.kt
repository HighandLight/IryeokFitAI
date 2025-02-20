package com.parkjunhyung.IryeokFitAi.repository

import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackPriorityRepository: JpaRepository<FeedbackPriority, Long> {
}