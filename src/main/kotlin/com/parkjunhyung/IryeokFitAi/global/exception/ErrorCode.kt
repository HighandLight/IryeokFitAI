package com.parkjunhyung.IryeokFitAi.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val message: String) {

    // 4XX
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이력서입니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리포트입니다."),
    REPORT_RESUME_NOT_LINKED(HttpStatus.BAD_REQUEST, "해당 리포트에 연결된 이력서가 없습니다."),
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 피드백입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 5XX
    TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류로 작업에 실패했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "현재 트래픽 폭주로 인해 요청을 처리할 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내 오류입니다."),

}
