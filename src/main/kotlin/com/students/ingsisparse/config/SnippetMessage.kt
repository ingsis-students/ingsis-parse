package com.students.ingsisparse.config

data class SnippetMessage(
    val snippetId: Long,
    val userId: Long,
    val jwtToken: String,
)
