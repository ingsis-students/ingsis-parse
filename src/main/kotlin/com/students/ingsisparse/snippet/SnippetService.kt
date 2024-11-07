package com.students.ingsisparse.snippet

import com.students.ingsisparse.types.Compliance
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SnippetService(private val restTemplate: RestTemplate) {
    fun updateStatus(id: Long, status: Compliance) {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(status, headers)

        restTemplate.exchange(
            "http://snippet-api:8080/api/snippets/$id/status",
            HttpMethod.PUT,
            entity,
            Void::class.java
        )
    }
}
