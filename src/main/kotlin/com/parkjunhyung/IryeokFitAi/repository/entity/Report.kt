package com.parkjunhyung.IryeokFitAi.repository.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Table(name = "report")
@Entity
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = true)
    var resume: Resume? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "title", nullable = true, length = 255)
    var title: String? = null,

    @Column(name = "job_posting_url", nullable = true, length = 500)
    var jobPostingUrl: String? = null,

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    var responsibilities: String? = null,

    @Column(name = "requirements", columnDefinition = "TEXT")
    var requirements: String? = null,

    @Column(name = "preferred", columnDefinition = "TEXT")
    var preferred: String? = null,

    @Column(name = "skills", columnDefinition = "TEXT")
    var skills: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.WAITING,

    @field:CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun onCreate() {
        this.createdAt = LocalDateTime.now()
    }
}
