package com.students.ingisisparse

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
internal class HttpRequestTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Test
    @Throws(Exception::class)
    fun greetingShouldReturnDefaultMessage() {
        Assertions.assertThat(
            restTemplate!!.getForObject(
                "http://localhost:$port/greeting",
                String::class.java
            )
        ).contains("Hello, World!")
    }
}
