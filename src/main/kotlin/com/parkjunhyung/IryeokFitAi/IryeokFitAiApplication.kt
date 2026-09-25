package com.parkjunhyung.IryeokFitAi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class IryeokFitAiApplication

fun main(args: Array<String>) {
	runApplication<IryeokFitAiApplication>(*args)
}
