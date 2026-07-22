package com.parkjunhyung.IryeokFitAi.global.exception

class CustomException(
    val errorCode: ErrorCode,
    detail: String? = null
) : RuntimeException(if (detail != null) "${errorCode.message} ($detail)" else errorCode.message)
