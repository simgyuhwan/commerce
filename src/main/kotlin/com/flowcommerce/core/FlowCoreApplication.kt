package com.flowcommerce.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlowCoreApplication

fun main(args: Array<String>) {
    runApplication<FlowCoreApplication>(*args)
}
