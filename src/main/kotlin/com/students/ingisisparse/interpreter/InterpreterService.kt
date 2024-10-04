package com.students.ingisisparse.interpreter

import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader
import java.util.LinkedList

@Service
class InterpreterService {
    /**
     * Interpret the given code and return a list with all the outputs
     * @param version the version of the language
     * @param code the code snippet to interpret
     * @return a list with all the outputs
     */
    fun interpret(version: String, code: String): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val printer = HttpPrinter()
        val inputProvider = HttpInputProvider(LinkedList())
        runner.execute(version, printer, inputProvider)
        return printer.prints
    }

    /**
     * Test the given code with the given inputs and return a list with all the results
     * @param version the version of the language
     * @param code the code snippet to test
     * @param inputs the inputs to test the code with
     * @param expectedOutputs the expected outputs
     * @return a list with all the errors. If there are no errors, the list will be empty
     */
    fun test(version: String, code: String, inputs: List<String>, expectedOutputs: List<String>): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val printer = HttpPrinter()
        val inputProvider = HttpInputProvider(LinkedList(inputs))
        runner.execute(version, printer, inputProvider)

        val results = compareOutputs(printer, expectedOutputs)

        return results
    }

    private fun compareOutputs(
        printer: HttpPrinter,
        expectedOutputs: List<String>
    ): List<String> {
        val actualOutputs = printer.prints
        val results = mutableListOf<String>()

        expectedOutputs.zip(actualOutputs) { expected, actual ->
            if (expected != actual) {
                results.add("Expected '$expected' but got '$actual'")
            }
        }

        if (expectedOutputs.size < actualOutputs.size) {
            for (i in expectedOutputs.size until actualOutputs.size) {
                results.add("Unexpected extra output: ${actualOutputs[i]}")
            }
        } else if (actualOutputs.size < expectedOutputs.size) {
            for (i in actualOutputs.size until expectedOutputs.size) {
                results.add("Missing expected output: ${expectedOutputs[i]}")
            }
        }

        return results
    }
}
