package com.students.ingsisparse.formatter

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.util.stream.Stream

@WebMvcTest(FormatterController::class)
internal class WebMockFormatterTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: FormatterService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Mock
    private lateinit var jwt: Jwt

    companion object {
        @JvmStatic
        fun formatterTestCases(): Stream<Arguments> {
            val formatterDir = File("src/test/resources/formatter")
            return formatterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("formatterTestCases")
    @DisplayName("Formatter Test Cases")
    @Throws(Exception::class)
    fun `test formatter cases`(version: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val rules = File(subDir, "rules.json").readText()
        val rulesJson = ObjectMapper().readTree(rules)
        val response = File(subDir, "response.txt").readText()

        val requestBody = ObjectMapper().writeValueAsString(FormatDto(version, code, rulesJson))
        Mockito.`when`(service.format(version, code, rulesJson.toString())).thenReturn(response)

        // Mock the JWT to have the required authority (scope)
        val jwt = Mockito.mock(Jwt::class.java)
        Mockito.`when`(jwt.audience).thenReturn(listOf("your-audience-here"))
        Mockito.`when`(jwt.getClaimAsString("scope")).thenReturn("read:snippets")

        Mockito.`when`(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/printscript/format")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}
