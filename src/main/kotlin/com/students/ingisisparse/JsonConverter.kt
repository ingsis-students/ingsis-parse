package com.students.ingisisparse

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.serialization.json.JsonObject

object JsonConverter {
    fun convertToKotlinxJson(jsonNode: Map<String, JsonNode>): JsonObject {
        val jsonMap = jsonNode.mapValues { entry ->
            kotlinx.serialization.json.Json.parseToJsonElement(entry.value.toString())
        }
        return JsonObject(jsonMap)
    }
}
