package com.students.ingisisparse.validate

import org.Runner
import org.springframework.stereotype.Service
import java.io.StringReader

@Service
class ValidateService {
    fun validate(version: String, code: String): List<String> {
        val reader = StringReader(code)
        val runner = Runner(version, reader)

        val result = runner.validate()
        return result.errorsList
    }
}
