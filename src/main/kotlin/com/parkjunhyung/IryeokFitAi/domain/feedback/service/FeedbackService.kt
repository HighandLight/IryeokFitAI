package com.parkjunhyung.IryeokFitAi.domain.feedback.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.parkjunhyung.IryeokFitAi.domain.feedback.repository.FeedbackCategoryRepository
import com.parkjunhyung.IryeokFitAi.domain.feedback.repository.FeedbackRepository
import com.parkjunhyung.IryeokFitAi.domain.report.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.domain.report.service.ReportService
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.ENUM.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.domain.report.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.Feedback
import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import io.awspring.cloud.s3.S3Template
import jakarta.transaction.Transactional
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val reportRepository: ReportRepository,
    private val feedbackCategoryRepository: FeedbackCategoryRepository,
    private val reportService: ReportService,
//    private val openAiChatModel: OpenAiChatModel
    private val chatModel: ChatModel, // 부하테스트 시 loadtest 프로파일의 가짜 ChatModel 빈으로 교체
    private val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String,
    @Value("\${spring.cloud.aws.s3.prompt-key:prompts/feedback-prompt.txt}")
    private val promptKey: String
) {
//    private val chatClient = ChatClient.create(openAiChatModel)
    private val chatClient = ChatClient.create(chatModel)


    @Transactional
    fun generateFeedback(reportId: Long): List<Feedback> {
        val report = reportRepository.findById(reportId)
            .orElseThrow { CustomException(ErrorCode.REPORT_NOT_FOUND, "report_id=$reportId") }
        val resume = report.resume

        val resumeText = resume?.resumeText ?: "(이력서 텍스트 없음)"
        val jobPostingText = """
        Title: ${report.title}
        URL: ${report.jobPostingUrl}
        RESPONSIBILITIES: ${report.responsibilities}
        REQUIREMENTS: ${report.requirements}
        PREFERRED: ${report.preferred}
        SKILLS: ${report.skills}
    """.trimIndent()

        val prompt = buildPrompt(jobPostingText, resumeText)

        val rawResponse = chatClient
            .prompt(prompt)
            .call()
            .content() ?: ""

        // 백틱 제거
        val cleanedResponse = rawResponse
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val newFeedbacks = parseFeedbackJson(cleanedResponse, report)

        //상태 변경 + WebSocket 알림 처리
        reportService.markAsCompleted(report.id)


        // 피드백 저장 + report 상태 변경
        report.status = ReportStatus.COMPLETED
        reportRepository.save(report)

        return feedbackRepository.saveAll(newFeedbacks)
    }


    private fun buildPrompt(jobPostingText: String, resumeText: String): String {
        return loadPromptTemplate()
            .replace("{{jobPosting}}", jobPostingText)
            .replace("{{resume}}", resumeText)
    }

    // 일단 매 요청마다 S3 호출 -> 캐싱 처리 필요할지?
    private fun loadPromptTemplate(): String {
        try {
            s3Template.download(bucketName, promptKey).inputStream.use {
                return it.readBytes().toString(StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            throw CustomException(ErrorCode.PROMPT_LOAD_FAILED, "key=$promptKey")
        }
    }


    private fun parseFeedbackJson(jsonString: String, report: Report): List<Feedback> {
        // JSON → DTO 변환할 임시 데이터 클래스
        data class FeedbackJson(
            val category: String,
            val priority: String,
            val detailText: String,
            val suggestionText: String
        )

        try {
            val mapper = jacksonObjectMapper()
            val feedbackItems: List<FeedbackJson> = mapper.readValue(jsonString)

            return feedbackItems.map { item ->
                val priority = FeedbackPriority.entries.find { it.name.equals(item.priority, ignoreCase = true) }
                    ?: FeedbackPriority.LOW

                Feedback(
                    report = report,
                    category = item.category,
                    priority = priority,
                    detailText = item.detailText,
                    suggestionText = item.suggestionText,
                    status = FeedbackStatus.PENDING,
                    createdAt = LocalDateTime.now()
                )
            }
        } catch (e: Exception) {
            println("Jackson 파싱 중 예외 발생: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    fun getFeedbackByReport(reportId: Long): List<Feedback> {
        return feedbackRepository.findByReportId(reportId)
    }

    @Transactional
    fun updateFeedbackStatus(feedbackId: Long, status: FeedbackStatus) {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { throw CustomException(ErrorCode.FEEDBACK_NOT_FOUND, "feedback_id=$feedbackId") }
        feedback.status = status
        feedbackRepository.save(feedback)
    }
}