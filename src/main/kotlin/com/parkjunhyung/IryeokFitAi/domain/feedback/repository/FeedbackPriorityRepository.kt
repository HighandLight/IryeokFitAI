package com.parkjunhyung.IryeokFitAi.domain.feedback.repository

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackPriority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackPriorityRepository: JpaRepository<FeedbackPriority, Long> {
}