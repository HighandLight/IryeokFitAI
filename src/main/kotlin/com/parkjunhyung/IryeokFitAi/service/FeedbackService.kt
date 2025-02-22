package com.parkjunhyung.IryeokFitAi.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.parkjunhyung.IryeokFitAi.repository.*
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import jakarta.transaction.Transactional
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val reportRepository: ReportRepository,
    private val feedbackPriorityRepository: FeedbackPriorityRepository,
    private val feedbackCategoryRepository: FeedbackCategoryRepository,
    private val openAiChatModel: OpenAiChatModel
) {
    private val chatClient = ChatClient.create(openAiChatModel)

    @Transactional
    fun generateFeedback(reportId: Long): List<Feedback> {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report 없음: $reportId") }
        val resume = report.resume

        val resumeText = resume.resumeText ?: "(이력서 텍스트 없음)"
        val jobPostingTitle = report.title
        val jobPostingUrl = report.jobPostingUrl
        val responsibilities = report.responsibilities
        val requirements = report.requirements
        val preferred = report.preferred
        val skills = report.skills

        val jobPostingText = """
            Title: $jobPostingTitle
            URL: $jobPostingUrl
            RESPONSIBILITIES: $responsibilities
            REQUIREMENTS: $requirements
            PREFERRED: $preferred
            SKILLS: $skills
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

        return feedbackRepository.saveAll(newFeedbacks)
    }

    private fun buildPrompt(jobPostingText: String, resumeText: String): String {
        return """
            You are a professional resume reviewer.
            The user wants detailed feedback on their resume based on the following job posting:

            [Job Posting]
            $jobPostingText

            [Resume Content]
            $resumeText

            Please return a JSON array of feedback objects. 
            Each object should have:
            {
              "category": "문서 형식 등",
              "priority": "HIGH / MEDIUM / LOW",
              "detailText": "세부 지적 사항",
              "suggestionText": "개선 제안"
            }

            Output only valid JSON, no extra commentary.
        """.trimIndent()
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
                val priorityEntity: FeedbackPriority? = feedbackPriorityRepository.findAll()
                    .find { it.level.equals(item.priority, ignoreCase = true) }
                    ?: feedbackPriorityRepository.findAll().find { it.level == "LOW" }

                Feedback(
                    report = report,
                    category = item.category,
                    priority = priorityEntity ?: throw IllegalStateException("No matching priority found"),
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
            .orElseThrow { throw IllegalArgumentException("피드백 없음!: $feedbackId") }
        feedback.status = status
        feedbackRepository.save(feedback)
    }
}