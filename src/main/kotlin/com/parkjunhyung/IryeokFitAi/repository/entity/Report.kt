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

    @field:CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
){
    @PrePersist
    fun onCreate() {
        this.createdAt = LocalDateTime.now()
    }
}

