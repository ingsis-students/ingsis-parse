package com.students.ingsisparse.formatter

import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader

@Service
class FormatterService {
    fun format(version: String, code: String, rules: String): String {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val result = runner.format(rules, version)
        return result.formattedCode
    }
}
