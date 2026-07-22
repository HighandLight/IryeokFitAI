package com.parkjunhyung.IryeokFitAi.domain.resume.entity

import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import jakarta.persistence.*

@Table(name = "resume")
@Entity
class Resume (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "original_file_path", nullable = false)
    val originalFilePath: String,

    @Column(name = "resume_text", columnDefinition = "TEXT")
    var resumeText: String? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ResumeStatus,

    @OneToMany(mappedBy = "resume", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val images: MutableList<ResumeImage> = mutableListOf()
) {
    fun markAsDeleted() {
        this.status = ResumeStatus.DELETED
    }
}
