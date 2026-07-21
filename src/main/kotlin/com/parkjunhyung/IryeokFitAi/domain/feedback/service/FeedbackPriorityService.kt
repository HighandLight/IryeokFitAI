package com.parkjunhyung.IryeokFitAi.domain.feedback.service

import com.parkjunhyung.IryeokFitAi.domain.feedback.repository.FeedbackPriorityRepository
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackPriority
import org.springframework.stereotype.Service

@Service
class FeedbackPriorityService (
    private val feedbackPriorityRepository: FeedbackPriorityRepository
) {
    fun getAllPriorities(): List<FeedbackPriority> {
        return feedbackPriorityRepository.findAll()
    }
}
