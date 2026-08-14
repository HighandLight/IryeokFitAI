package com.parkjunhyung.IryeokFitAi.global.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

// @Transactional 메서드 실행 시간 측정용 로그
@Aspect
@Component
//@Profile("loadtest")  // 실제 개발 서버 prod 레벨의 OpenAI 호출 TS 시간 측정 위해 비활성화
class TransactionTimingAspect {
    private val log = LoggerFactory.getLogger(TransactionTimingAspect::class.java)

    @Around("@annotation(jakarta.transaction.Transactional)")
    fun measure(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.nanoTime()
        try {
            return joinPoint.proceed()
        } finally {
            val elapsedMs = (System.nanoTime() - start) / 1_000_000 // (ns -> ms)
            val signature = joinPoint.signature
            val args = joinPoint.args.joinToString(", ")
            log.info(
                "[PERF-TS] {}.{}({}) - {}ms - thread={}",
                signature.declaringType.simpleName, signature.name, args, elapsedMs, Thread.currentThread().name
            )
        }
    }
}
