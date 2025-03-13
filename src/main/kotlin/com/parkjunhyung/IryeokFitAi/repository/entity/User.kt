package com.parkjunhyung.IryeokFitAi.repository.entity

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus
import jakarta.persistence.*

@Table(name = "user")
@Entity
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phoneNumber: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.UNVERIFIED
)