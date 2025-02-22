package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.FeedbackRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import jakarta.transaction.Transactional
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.stereotype.Service

@Service
class FeedbackService (
    private val feedbackRepository: FeedbackRepository,
    private val openAiChatModel: OpenAiChatModel
) {
    val chatClient = ChatClient.create(openAiChatModel)

    fun whatTimeIsIt(): String? {
        return chatClient.prompt("What time is it now?").call().content()
    }

    fun getFeedbackByReport(reportId: Long): List<Feedback> {
        return feedbackRepository.findByReportId(reportId)
    }

    @Transactional
    fun updateFeedbackStatus(feedbackId: Long, status: FeedbackStatus) {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { throw IllegalArgumentException("피드백 없음!: $feedbackId")}
        feedback.status = status
        feedbackRepository.save(feedback)
    }

}