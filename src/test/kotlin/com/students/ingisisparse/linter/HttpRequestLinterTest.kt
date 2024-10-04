package com.students.ingisisparse.linter

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
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
import java.io.File
import java.util.stream.Stream

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
internal class HttpRequestLinterTest {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    companion object {
        @JvmStatic
        fun linterTestCases(): Stream<Arguments> {
            val linterDir = File("src/test/resources/linter")
            return linterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("linterTestCases")
    @DisplayName("Linter Test Cases")
    @Throws(Exception::class)
    fun `test linter cases`(version: String, name: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val rules = File(subDir, "rules.json").readText()
        val rulesJson = Json.parseToJsonElement(rules).jsonObject
        val response = File(subDir, "response.txt").readText()

        val requestBody = LintDto(version, code, JsonConverter.convertToJacksonJson(rulesJson))

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val entity = HttpEntity(requestBody, headers)

        val url = "http://localhost:$port/analyze"
        val result = restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).contains(response)
    }
}
