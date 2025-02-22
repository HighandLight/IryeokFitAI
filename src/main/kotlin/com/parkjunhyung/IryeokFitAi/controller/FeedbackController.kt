package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.FeedbackDto
import com.parkjunhyung.IryeokFitAi.dto.toFeedbackDto
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import com.parkjunhyung.IryeokFitAi.service.FeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/feedbacks")
class FeedbackController (
    private val feedbackService: FeedbackService
) {
    @GetMapping("/{reportId}")
    fun getFeedbacksByReportId(@PathVariable reportId: Long): ResponseEntity<List<FeedbackDto>> {
        val feedbacks = feedbackService.getFeedbackByReport(reportId)
            .map { it.toFeedbackDto() }
        return ResponseEntity.ok(feedbacks)
    }

    @GetMapping("/report/{reportId}")
    fun getFeedbackByReport(@PathVariable reportId: Long): ResponseEntity<List<Feedback>> {
        return ResponseEntity.ok(feedbackService.getFeedbackByReport(reportId))
    }

    @PatchMapping("/{feedbackId}/status")
    fun updateFeedbackStatus(@PathVariable feedbackId: Long, @RequestParam status: FeedbackStatus): ResponseEntity<Void> {
        feedbackService.updateFeedbackStatus(feedbackId, status)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/generate/{reportId}")
    fun generateFeedbackForReport(@PathVariable reportId: Long): ResponseEntity<List<FeedbackDto>> {
        val newFeedbacks = feedbackService.generateFeedback(reportId)
        val dtoList = newFeedbacks.map { it.toFeedbackDto() }
        return ResponseEntity.ok(dtoList)
    }
}