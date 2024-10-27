package com.students.ingsisparse.linter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsisparse.types.Rule
import kotlinx.serialization.json.JsonObject
import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader

@Service
class LinterService {
    fun analyze(version: String, code: String, rules: JsonObject): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val result = runner.analyze(rules)
        return result.warningsList
    }

    fun convertActiveRulesToJsonObject(rules: List<Rule>): JsonObject {
        /**
        * Convert active rules to a map of rule name to rule value
         **/
        val objectMapper = ObjectMapper()

        val activeRuleMap = rules
            .filter { it.isActive }
            .associate { rule -> rule.name to objectMapper.valueToTree<JsonNode>(rule.value) }

        return JsonConverter.convertToKotlinxJson(activeRuleMap)
    }
}
