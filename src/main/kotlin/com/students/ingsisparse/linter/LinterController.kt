package com.students.ingsisparse.linter

import com.students.ingsisparse.linter.producers.LinterRuleProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/printscript")
class LinterController(private val linterService: LinterService,
                       private val lintRuleProducer: LinterRuleProducer,
) {

    @PostMapping("/analyze")
    fun analyzeCode(@RequestBody lintDto: LintDto): List<String> {
        val version = lintDto.version
        val code = lintDto.code
        val rules = JsonConverter.convertToKotlinxJson(lintDto.rules)

        return linterService.analyze(version, code, rules)
    }

    @PostMapping("/rules/lint")
    suspend fun addLintingRule(@RequestBody lintDto: LintDto): String {
        lintRuleProducer.publishEvent(lintDto)
        return "New linting rule added"
    }
}
