package com.parkjunhyung.IryeokFitAi.repository.entity

import jakarta.persistence.*

@Table(name = "feedback_priority")
@Entity
class FeedbackPriority (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "level", nullable = false, length = 50)
    val level: String,

)