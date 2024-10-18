package com.students.ingsisparse

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IngisisParseApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()
    dotenv.entries().forEach { System.setProperty(it.key, it.value) }
    runApplication<IngisisParseApplication>(*args)
}
