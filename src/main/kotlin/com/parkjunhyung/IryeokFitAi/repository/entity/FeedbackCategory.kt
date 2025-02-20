package com.parkjunhyung.IryeokFitAi.repository.entity

import jakarta.persistence.*

@Table(name = "feedback_category")
@Entity
class FeedbackCategory (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "name", nullable = false, length = 50)
    val name: String

)