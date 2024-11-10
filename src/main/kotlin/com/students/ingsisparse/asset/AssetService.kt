package com.students.ingsisparse.asset

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) {
    fun get(directory: String, id: Long): String {
        val response = restTemplate.getForObject(
            "http://asset-api:8080/v1/asset/$directory/$id",
            String::class.java
        )
        return response ?: "Search in $directory not found"
    }

    fun put(directory: String, id: Long, content: String): String {
        restTemplate.put(
            "http://asset-api:8080/v1/asset/$directory/$id",
            content,
            String::class.java
        )
        return "Snippet updated"
    }
}
