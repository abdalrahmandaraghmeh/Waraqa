package com.waraqa.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@SpringBootApplication
@EnableJdbcRepositories(basePackages = ["com.waraqa.backend.repository"])
class WaraqaApplication

fun main(args: Array<String>) {
    runApplication<WaraqaApplication>(*args)
}
