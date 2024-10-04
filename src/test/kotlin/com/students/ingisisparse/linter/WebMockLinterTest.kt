package com.students.ingisisparse.linter

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.util.stream.Stream

@WebMvcTest(LinterController::class)
internal class WebMockLinterTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: LinterService

    companion object {
        @JvmStatic
        fun linterTestCases(): Stream<Arguments> {
            val linterDir = File("src/test/resources/linter")
            return linterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("linterTestCases")
    @DisplayName("Linter Test Cases")
    @Throws(Exception::class)
    fun `test linter cases`(version: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val rules = File(subDir, "rules.json").readText()
        val rulesJson = Json.parseToJsonElement(rules).jsonObject
        val response = File(subDir, "response.txt").readText()

        val requestBody = ObjectMapper().writeValueAsString(
            LintDto(version, code, JsonConverter.convertToJacksonJson(rulesJson))
        )

        Mockito.`when`(service.analyze(version, code, rulesJson)).thenReturn(listOf(response))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}
