package com.students.ingsisparse.formatter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsisparse.types.Rule
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

    fun getActiveAdaptedRules(
        formatRulesJson: String
    ): String {
        val objectMapper = jacksonObjectMapper()
        val formatRules: List<Rule> = objectMapper.readValue(formatRulesJson, object : TypeReference<List<Rule>>() {})

        val rulesMap = mutableMapOf<String, Any?>()
        formatRules.forEach { rule ->
            if (rule.isActive) {
                val key = camelToSnakeCase(rule.name)
                rulesMap[key] = rule.value
            }
        }

        return objectMapper.writeValueAsString(rulesMap)
    }

    private fun camelToSnakeCase(camelCase: String): String {
        return camelCase
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
    }

}
