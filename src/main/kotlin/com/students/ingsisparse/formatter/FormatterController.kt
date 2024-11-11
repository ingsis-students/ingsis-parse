package com.students.ingsisparse.formatter

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping

@RestController
@RequestMapping("/api/printscript")
class FormatterController(private val formatterService: FormatterService) {

    @PostMapping("/format")
    fun formatCode(
        @RequestHeader("Authorization") token: String,
        @RequestBody formatDto: FormatDto
    ): String {
        val version = formatDto.version
        val code = formatDto.code
        val rules = formatDto.rules.toString()
        val activeRules = formatterService.getActiveAdaptedRules(rules)

        return formatterService.format(version, code, activeRules)
    }
}
