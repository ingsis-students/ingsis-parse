package com.students.ingsisparse.config

import kotlinx.serialization.json.JsonObject

data class SnippetMessage(
    val snippetId: Long,
    val rules: JsonObject
)
