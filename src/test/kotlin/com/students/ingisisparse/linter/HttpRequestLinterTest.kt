package com.students.ingisisparse.linter

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
internal class HttpRequestLinterTest {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @Throws(Exception::class)
    fun `test invalid snake case`() {
        // Read input data from files
        val version = "1.0"
        val code = File("src/test/resources/linter/invalid-snake-case/code.txt").readText()
        val rules = File("src/test/resources/linter/invalid-snake-case/rules.json").readText()
        val rulesJson = Json.parseToJsonElement(rules).jsonObject
        val response = File("src/test/resources/linter/invalid-snake-case/response.txt").readText()

        val requestBody = """{"version": "$version", "code": "$code", "rules": $rulesJson}"""

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
