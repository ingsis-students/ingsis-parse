package com.students.ingisisparse.interpreter

import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader
import java.util.LinkedList

@Service
class InterpreterService {
    fun interpret(version: String, code: String): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val printer = HttpPrinter()
        val inputProvider = HttpInputProvider(LinkedList())
        runner.execute(version, printer, inputProvider)
        return printer.prints
    }
}
