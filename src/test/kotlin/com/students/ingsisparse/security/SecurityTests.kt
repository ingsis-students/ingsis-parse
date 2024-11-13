package com.students.ingsisparse.security

import com.students.ingsisparse.interpreter.HttpInputProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2Error
import java.util.LinkedList
import java.util.Queue

@ActiveProfiles("test")
class SecurityTests {

    private val audience = "https://students.ingsis.com/api"
    private val audienceValidator = AudienceValidator(audience)

    @Test
    fun `audience validator success`() {
        val jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .audience(listOf(audience))
            .build()

        val result = audienceValidator.validate(jwt)
        assertEquals(OAuth2TokenValidatorResult.success(), result, "The validation should succeed when the audience matches.")
    }

    @Test
    fun `audience validator failure`() {
        val jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .audience(listOf("https://other-audience.com"))
            .build()

        val result = audienceValidator.validate(jwt)

        val expectedError = OAuth2Error("invalid_token", "The required audience is missing", null)
        val actualError = result.errors.firstOrNull()
        assertEquals(expectedError.errorCode, actualError?.errorCode)
        assertEquals(expectedError.description, actualError?.description)
    }

    @Test
    fun `input returns the next element from the queue`() {
        val queue: Queue<String> = LinkedList(listOf("first", "second", "third"))
        val inputProvider = HttpInputProvider(queue)

        assertEquals("first", inputProvider.input())
        assertEquals("second", inputProvider.input())
        assertEquals("third", inputProvider.input())
    }
}
