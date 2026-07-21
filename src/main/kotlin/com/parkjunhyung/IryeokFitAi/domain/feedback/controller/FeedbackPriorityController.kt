package com.parkjunhyung.IryeokFitAi.domain.feedback.controller

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.Feedback
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.domain.feedback.service.FeedbackPriorityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feedback-priorities")
class FeedbackPriorityController (
    private val feedbackPriorityService: FeedbackPriorityService
){
    @GetMapping
    fun getAllPriorities(): ResponseEntity<List<FeedbackPriority>> {
        return ResponseEntity.ok(feedbackPriorityService.getAllPriorities())
    }
}