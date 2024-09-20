package com.students.ingisisparse

import com.students.ingisisparse.greeting.GreetingController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class SmokeTest {
    @Autowired
    private val controller: GreetingController? = null

    @Test
    @Throws(Exception::class)
    fun contextLoads() {
        assertThat(controller).isNotNull()
    }
}
