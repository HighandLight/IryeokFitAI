package com.parkjunhyung.IryeokFitAi.repository.entity

import jakarta.persistence.*

@Table(name = "report")
@Entity
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
)
