package com.students.ingsisparse.asset

import com.students.ingsissnippet.constants.ASSETSERVICE_URL
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) {
    fun get(directory: String, id: Long): String {
        val response = restTemplate.getForObject(
            "$ASSETSERVICE_URL/$directory/$id",
            String::class.java
        )
        return response ?: "Search in $directory not found"
    }

    fun put(directory: String, id: Long, content: String): String {
        restTemplate.put(
            "$ASSETSERVICE_URL/$directory/$id",
            content,
            String::class.java
        )
        return "Snippet updated"
    }
}
