package com.students.ingsisparse.linter

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/printscript")
class LinterController(private val linterService: LinterService) {

    @PostMapping("/analyze")
    fun analyzeCode(@RequestBody lintDto: LintDto): List<String> {
        val version = lintDto.version
        val code = lintDto.code
        val rules = JsonConverter.convertToKotlinxJson(lintDto.rules)

        return linterService.analyze(version, code, rules)
    }
}
