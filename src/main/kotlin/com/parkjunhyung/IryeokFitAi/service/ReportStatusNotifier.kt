package com.parkjunhyung.IryeokFitAi.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ReportStatusNotifier(
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun notifyStatusCompleted(reportId: Long) {
        messagingTemplate.convertAndSend("/topic/reports/$reportId", mapOf("status" to "COMPLETED"))
    }
}

