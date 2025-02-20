package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import com.parkjunhyung.IryeokFitAi.service.FeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feedbacks")
class FeedbackController (
    private val feedbackService: FeedbackService
) {
    @GetMapping("/report/{reportId}")
    fun getFeedbackByReport(@PathVariable reportId: Long): ResponseEntity<List<Feedback>> {
        return ResponseEntity.ok(feedbackService.getFeedbackByReport(reportId))
    }

    @PatchMapping("/{feedbackId}/status")
    fun updateFeedbackStatus(@PathVariable feedbackId: Long, @RequestParam status: FeedbackStatus): ResponseEntity<Void> {
        feedbackService.updateFeedbackStatus(feedbackId, status)
        return ResponseEntity.noContent().build()
    }
}