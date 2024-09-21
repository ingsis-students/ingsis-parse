package com.students.ingisisparse.formatter

import com.fasterxml.jackson.databind.JsonNode

data class FormatDto(
    val version: String,
    val code: String,
    val rules: JsonNode
)
