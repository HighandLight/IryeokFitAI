package com.parkjunhyung.IryeokFitAi.repository.entity

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "report")
@Entity
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    val resume: Resume,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "title", nullable = false, length = 255)
    val title: String,

    @Column(name = "job_posting_url", nullable = false, length = 500)
    val jobPostingUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.SAVED,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()


)
