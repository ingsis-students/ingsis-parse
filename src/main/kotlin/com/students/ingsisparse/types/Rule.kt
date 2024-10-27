package com.students.ingsisparse.types

data class Rule(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val value: Any? = null
)
