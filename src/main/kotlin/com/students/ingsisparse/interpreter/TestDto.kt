package com.students.ingsisparse.interpreter

data class TestDto(
    val version: String,
    val snippetId: Long,
    val inputs: List<String>,
    val outputs: List<String>
)
