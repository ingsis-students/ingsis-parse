package com.students.ingisisparse

import com.students.ingisisparse.greeting.GreetingController
import com.students.ingisisparse.greeting.GreetingService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(GreetingController::class)
internal class WebMockTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @MockBean
    private val service: GreetingService? = null

    @Test
    @Throws(Exception::class)
    fun greetingShouldReturnMessageFromService() {
        Mockito.`when`(service!!.greet()).thenReturn("Hello, Mock")
        mockMvc!!.perform(MockMvcRequestBuilders.get("/greeting")).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Hello, Mock")))
    }
}
