package com.parkjunhyung.IryeokFitAi.global.config

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

// loadtest 프로파일-> Openai 호출 없이 FeedbackService.generateFeedback 흐름을 끝까지 태우기 위한 스텁.
@Configuration
@Profile("loadtest")
class LoadTestChatConfig {

    @Bean
    fun fakeChatModel(): ChatModel = ChatModel { _: Prompt ->
        Thread.sleep((5000..7500).random().toLong()) // AOP 기반 측정 평균(6s) 반영
        val stubJson = """
            [{"category":"부하테스트 측정용 피드백","priority":"LOW","detailText":"백엔드 개발자","suggestionText":"박준형"}]
        """.trimIndent()
        ChatResponse(listOf(Generation(AssistantMessage(stubJson))))
    }
}
