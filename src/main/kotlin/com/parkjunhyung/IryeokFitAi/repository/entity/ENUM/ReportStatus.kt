package com.parkjunhyung.IryeokFitAi.repository.entity.ENUM

enum class ReportStatus {
    SAVED,       // 생성만 된 상태 (아직 피드백은 생성 x)
    WAITING,     // 피드백 생성 중
    COMPLETED,   // 피드백 완료
    DELETED      // 삭제됨
}