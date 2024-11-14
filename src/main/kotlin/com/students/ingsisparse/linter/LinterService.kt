package com.students.ingsisparse.linter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
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

        // create the jackson representation
        val activeRuleMap: Map<String, JsonNode> = rules
            .filter { it.isActive }
            .associate { rule ->
                val key = rule.name
                val value: JsonNode = when (rule.value) {
                    null -> when (key) {
                        "PrintUseCheck" -> {
                            val printUseNode = objectMapper.createObjectNode()
                            printUseNode.put("printlnCheckEnabled", true)
                            printUseNode
                        }
                        "ReadInputCheck" -> {
                            val readInputNode = objectMapper.createObjectNode()
                            readInputNode.put("readInputCheckEnabled", true)
                            readInputNode
                        }
                        else -> JsonNodeFactory.instance.nullNode()
                    }
                    is String -> when (key) {
                        "NamingFormatCheck" -> {
                            val namingPatternNode = objectMapper.createObjectNode()
                            namingPatternNode.put("namingPatternName", rule.value)
                            namingPatternNode
                        }
                        else -> JsonNodeFactory.instance.textNode(rule.value)
                    }
                    else -> objectMapper.valueToTree(rule.value)
                }
                key to value
            }
        return JsonConverter.convertToKotlinxJson(activeRuleMap) // json representation
    }
}
