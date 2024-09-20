package com.students.ingisisparse.interpreter

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class InterpreterController(private val interpreterService: InterpreterService) {

    @PostMapping("/interpret")
    fun interpret(@RequestBody interpretDto: InterpretDto): List<String> {
        val version = interpretDto.version
        val code = interpretDto.code

        return interpreterService.interpret(version, code)
    }
}
