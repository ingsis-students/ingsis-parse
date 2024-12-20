package com.students.ingsisparse.interpreter

import com.students.ingsisparse.asset.AssetService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
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
internal class HttpRequestInterpreterTest {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockBean
    private lateinit var assetService: AssetService

    @BeforeEach
    fun setUp() {
        whenever(assetService.get("snippets", 1)).thenReturn("println(1);")
    }

    companion object {
        @JvmStatic
        fun interpreterTestCases(): Stream<Arguments> {
            val interpreterDir = File("src/test/resources/interpreter")
            return interpreterDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }

        @JvmStatic
        fun testEndpointTestCases(): Stream<Arguments> {
            val testDir = File("src/test/resources/test")
            return testDir.listFiles { file -> file.isDirectory }?.flatMap { versionDir ->
                versionDir.listFiles { file -> file.isDirectory }?.map { subDir ->
                    Arguments.of(versionDir.name, subDir.name, subDir)
                } ?: emptyList()
            }?.stream() ?: Stream.empty()
        }
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("interpreterTestCases")
    @DisplayName("Interpreter Test Cases")
    @Throws(Exception::class)
    fun `test interpreter cases`(version: String, name: String, subDir: File) {
        val code = File(subDir, "code.txt").readText()
        val response = File(subDir, "response.txt").readText()

        val requestBody = InterpretDto(version, code)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer mocked-jwt-token")
        }

        val entity = HttpEntity(requestBody, headers)

        val url = "http://localhost:$port/api/printscript/interpret"
        val result = restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).contains(response)
    }

    @ParameterizedTest(name = "version {0} - {1}")
    @MethodSource("testEndpointTestCases")
    @DisplayName("Test Endpoint Test Cases")
    @Throws(Exception::class)
    fun `test endpoint cases`(version: String, name: String, subDir: File) {
        val code = File(subDir, "code.txt").readText().toLong()
        val inputs = File(subDir, "inputs.txt").readLines()
        val outputs = File(subDir, "outputs.txt").readLines()
        val response = File(subDir, "response.txt").readText()

        val requestBody = TestDto(version, code, inputs, outputs)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer mocked-jwt-token")
        }

        val entity = HttpEntity(requestBody, headers)

        val url = "http://localhost:$port/api/printscript/test"
        val result = restTemplate.exchange(url, HttpMethod.POST, entity, String::class.java)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).contains(response)
    }
}
