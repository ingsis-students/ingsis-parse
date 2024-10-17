package com.students.ingsisparse.linter

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.serialization.json.JsonObject

object JsonConverter {
    fun convertToKotlinxJson(jsonNode: Map<String, JsonNode>): JsonObject {
        val jsonMap = jsonNode.mapValues { entry ->
            kotlinx.serialization.json.Json.parseToJsonElement(entry.value.toString())
        }
        return JsonObject(jsonMap)
    }

    fun convertToJacksonJson(jsonObject: JsonObject): Map<String, JsonNode> {
        val jsonMap = jsonObject.mapValues { entry ->
            com.fasterxml.jackson.databind.ObjectMapper().readTree(entry.value.toString())
        }
        return jsonMap
    }
}
