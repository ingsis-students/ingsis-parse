package com.students.ingsisparse.linter

import kotlinx.serialization.json.JsonObject
import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader

@Service
class LinterService {
    fun analyze(version: String, code: String, rules: JsonObject): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val result = runner.analyze(rules)
        return result.warningsList
    }
}
