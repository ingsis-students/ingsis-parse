package com.students.ingisisparse.linter

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import java.io.File
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.http.MediaType

@WebMvcTest(LinterController::class)
internal class WebMockLinterTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: LinterService

    @Test
    @Throws(Exception::class)
    fun `test invalid snake case`() {
        val version = "1.0"
        val code = File("src/test/resources/linter/invalid-snake-case/code.txt").readText()
        val rules = File("src/test/resources/linter/invalid-snake-case/rules.json").readText()
        val rulesJson = Json.parseToJsonElement(rules).jsonObject

        val response = File("src/test/resources/linter/invalid-snake-case/response.txt").readText()

        val requestBody = """{"version": "$version", "code": "$code", "rules": $rulesJson}"""

        Mockito.`when`(service.analyze(version, code, rulesJson)).thenReturn(listOf(response))

        mockMvc.perform(MockMvcRequestBuilders.post("/analyze")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}