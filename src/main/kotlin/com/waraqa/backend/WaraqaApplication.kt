package com.waraqa.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WaraqaApplication

fun main(args: Array<String>) {
    runApplication<WaraqaApplication>(*args)
}
