package com.parkjunhyung.IryeokFitAi.global.monitoring

import com.zaxxer.hikari.HikariDataSource
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ResourceMetricsLogger(
    private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(
        fixedRateString = "\${monitoring.resource-metrics.interval-ms:30000}"
    )
    fun logConnectionPoolMetrics() {
        val hikariDataSource = dataSource.unwrap(HikariDataSource::class.java)
        val pool = hikariDataSource.hikariPoolMXBean ?: return

        log.info(
            "HikariCP resource metrics: {}, {}, {}, {}, {}",
            kv("metricType", "resource"),
            kv("hikariActive", pool.activeConnections), // 사용중 Connection 수
            kv("hikariIdle", pool.idleConnections), // Pool 대기중 Connection 수
            kv("hikariPending", pool.threadsAwaitingConnection), // Connection 획득 대기중 Thread 수
            kv("hikariTotal", pool.totalConnections) // 전체 Connection 수
        )
    }
}