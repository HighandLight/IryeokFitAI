package com.parkjunhyung.IryeokFitAi.repository.entity

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "feedback")
@Entity
class Feedback (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    val report: Report,

    @Column(name = "category", nullable = false, length = 100)
    var category: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id", nullable = false)
    val priority: FeedbackPriority,

    @Column(name = "detail_text", nullable = false, columnDefinition = "TEXT")
    val detailText: String,

    @Column(name = "suggestion_text", nullable = false, columnDefinition = "TEXT")
    val suggestionText: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: FeedbackStatus = FeedbackStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    )