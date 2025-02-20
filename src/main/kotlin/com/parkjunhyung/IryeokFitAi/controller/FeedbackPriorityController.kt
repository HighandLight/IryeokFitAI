package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.service.FeedbackPriorityService
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