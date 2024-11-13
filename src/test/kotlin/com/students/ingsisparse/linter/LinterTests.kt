package com.students.ingsisparse.linter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.students.ingsisparse.types.Rule
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test
import kotlin.test.assertEquals

@ActiveProfiles("test")
class LinterTests {

    private val linterService = LinterService()

    @Test
    fun `test convertActiveRulesToJsonObject`() {
        val rules = listOf(
            Rule(name = "PrintUseCheck", isActive = true, value = null),
            Rule(name = "ReadInputCheck", isActive = true, value = null),
            Rule(name = "NamingFormatCheck", isActive = true, value = "camelCase"),
            Rule(name = "SomeInactiveRule", isActive = false, value = "shouldNotBeIncluded"),
            Rule(name = "SpaceBeforeColon", isActive = true, value = true)
        )

        val jsonObject = linterService.convertActiveRulesToJsonObject(rules)
        val expectedJsonNode: ObjectNode = JsonNodeFactory.instance.objectNode().apply {
            putObject("PrintUseCheck").put("printlnCheckEnabled", true)
            putObject("ReadInputCheck").put("readInputCheckEnabled", true)
            putObject("NamingFormatCheck").put("namingPatternName", "camelCase")
            put("SpaceBeforeColon", true)
        }

        val expectedMap: Map<String, JsonNode> = expectedJsonNode.fields().asSequence().associate { it.key to it.value }
        val expectedJson = JsonConverter.convertToKotlinxJson(expectedMap)

        assertEquals(expectedJson, jsonObject)
    }
}
