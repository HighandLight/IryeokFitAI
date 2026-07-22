package com.parkjunhyung.IryeokFitAi.domain.resume.entity

import jakarta.persistence.*

@Table(name = "resume_image")
@Entity
class ResumeImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    val resume: Resume,

    @Column(name = "page_number", nullable = false)
    val pageNumber: Int,

    @Column(name = "image_url", nullable = false, length = 500)
    val imageUrl: String
)
