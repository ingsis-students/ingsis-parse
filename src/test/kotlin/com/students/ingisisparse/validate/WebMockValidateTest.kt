package com.students.ingisisparse.validate

import com.fasterxml.jackson.databind.ObjectMapper
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

@WebMvcTest(ValidateController::class)
internal class WebMockValidateTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: ValidateService

    companion object {
        @JvmStatic
        fun validateTestCases(): Stream<Arguments> {
            val validateDir = File("src/test/resources/validate")
            return validateDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("validateTestCases")
    @DisplayName("Validate Test Cases")
    @Throws(Exception::class)
    fun `test validate cases`(version: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val response = File(subDir, "response.txt").readText()

        val requestBody = ObjectMapper().writeValueAsString(ValidateDto(version, code))

        Mockito.`when`(service.validate(version, code)).thenReturn(listOf(response))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}
