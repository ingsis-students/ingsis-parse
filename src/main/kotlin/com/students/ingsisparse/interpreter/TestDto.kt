package com.students.ingsisparse.interpreter

data class TestDto(
    val version: String,
    val code: String,
    val inputs: List<String>,
    val outputs: List<String>
)
