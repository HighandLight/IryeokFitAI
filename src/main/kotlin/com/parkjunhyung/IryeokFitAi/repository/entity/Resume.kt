package com.parkjunhyung.IryeokFitAi.repository.entity

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

    @Column(name = "converted_image_path")
    val convertedImagePath: String? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ResumeStatus
) {
    fun markAsDeleted() {
        this.status = ResumeStatus.DELETED
    }
}
