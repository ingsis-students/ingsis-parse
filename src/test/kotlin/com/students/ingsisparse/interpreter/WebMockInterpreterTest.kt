package com.students.ingsisparse.interpreter

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

@WebMvcTest(InterpreterController::class)
internal class WebMockInterpreterTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: InterpreterService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Mock
    private lateinit var jwt: Jwt

    companion object {
        @JvmStatic
        fun interpreterTestCases(): Stream<Arguments> {
            val interpreterDir = File("src/test/resources/interpreter")
            return interpreterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("interpreterTestCases")
    @DisplayName("Interpreter Test Cases")
    @Throws(Exception::class)
    fun `test interpreter cases`(version: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val response = File(subDir, "response.txt").readText()

        val requestBody = ObjectMapper().writeValueAsString(InterpretDto(version, code))

        Mockito.`when`(service.interpret(version, code)).thenReturn(listOf(response))

        // Mock the JWT to have the required authority (scope)
        val jwt = Mockito.mock(Jwt::class.java)
        Mockito.`when`(jwt.audience).thenReturn(listOf("your-audience-here"))
        Mockito.`when`(jwt.getClaimAsString("scope")).thenReturn("read:snippets")

        Mockito.`when`(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/printscript/interpret")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(response)))
    }
}
