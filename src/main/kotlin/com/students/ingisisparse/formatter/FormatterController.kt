package com.students.ingisisparse.formatter

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FormatterController(private val formatterService: FormatterService) {

    @PostMapping("/format")
    fun formatCode(@RequestBody formatDto: FormatDto): String {
        val version = formatDto.version
        val code = formatDto.code
        val rules = formatDto.rules.toString()

        return formatterService.format(version, code, rules)
    }
}
