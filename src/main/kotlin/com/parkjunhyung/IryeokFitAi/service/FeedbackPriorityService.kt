package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.FeedbackPriorityRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority
import org.springframework.stereotype.Service

@Service
class FeedbackPriorityService (
    private val feedbackPriorityRepository: FeedbackPriorityRepository
) {
    fun getAllPriorities(): List<FeedbackPriority> {
        return feedbackPriorityRepository.findAll()
    }
}
