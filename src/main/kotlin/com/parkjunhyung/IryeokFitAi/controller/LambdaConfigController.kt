package com.parkjunhyung.IryeokFitAi.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config")
class LambdaConfigController (
    @Value("\${spring.lambda.function.url}")
    private val lambdaFunctionUrl: String
) {
    @GetMapping("/lambda-url")
    fun getLambdUrl(): Map<String,String> {
        return mapOf("lambdaFunctionUrl" to lambdaFunctionUrl)
    }
}