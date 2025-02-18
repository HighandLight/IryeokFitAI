package com.parkjunhyung.IryeokFitAi.repository.entity

import jakarta.persistence.*

@Table(name = "user")
@Entity
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    val email: String,
    val password: String,
)