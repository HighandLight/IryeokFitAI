package com.parkjunhyung.IryeokFitAi.util

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordUtil {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())  // 12회 해싱
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword.toCharArray()).verified
    }
}
