package com.students.ingisisparse.linter

import com.fasterxml.jackson.databind.JsonNode

data class LintDto(
    val version: String,
    val code: String,
    val rules: Map<String, JsonNode>
)
