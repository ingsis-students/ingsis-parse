package com.students.ingsisparse.linter

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.util.stream.Stream

@WebMvcTest(LinterController::class)
@ActiveProfiles("test")
internal class WebMockLinterTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: LinterService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Mock
    private lateinit var jwt: Jwt

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

        // Mock the JWT to have the required authority (scope)
        val jwt = Mockito.mock(Jwt::class.java)
        Mockito.`when`(jwt.audience).thenReturn(listOf("your-audience-here"))
        Mockito.`when`(jwt.getClaimAsString("scope")).thenReturn("read:snippets")

        Mockito.`when`(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/printscript/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}
