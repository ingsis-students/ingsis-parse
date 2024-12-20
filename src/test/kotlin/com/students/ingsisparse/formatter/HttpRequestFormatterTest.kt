package com.students.ingsisparse.formatter

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.util.stream.Stream

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class HttpRequestFormatterTest {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    companion object {
        @JvmStatic
        fun formatterTestCases(): Stream<Arguments> {
            val interpreterDir = File("src/test/resources/formatter")
            return interpreterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("formatterTestCases")
    @DisplayName("Formatter Test Cases")
    @Throws(Exception::class)
    fun `test formatter cases`(version: String, name: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val rules = File(subDir, "rules.json").readText()
        val rulesJson = ObjectMapper().readTree(rules)

        val requestBody = FormatDto(version, code, rulesJson)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer mocked-jwt-token")
        }

        val entity = HttpEntity(requestBody, headers)

        val url = "http://localhost:$port/api/printscript/format"
        val result = restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }
}
