package com.students.ingsisparse.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsisparse.asset.AssetService
import com.students.ingsisparse.config.consumers.FormatRuleConsumer
import com.students.ingsisparse.formatter.FormatterService
import com.students.ingsisparse.snippet.SnippetService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class FormatRuleConsumerTest {

    private val redisTemplate = mock<ReactiveRedisTemplate<String, String>>()
    private val formatService = mock<FormatterService>()
    private val assetService = mock<AssetService>()
    private val snippetService = mock<SnippetService>()
    private val consumer = FormatRuleConsumer(
        redisTemplate,
        "testStreamKey",
        "testGroupId",
        formatService,
        assetService,
        snippetService
    )

    @Test
    fun `should format code successfully`() {
        val message = SnippetMessage(1L, 1L, "v1", "jwt-token")
        val record = mock<ObjectRecord<String, String>>()
        whenever(record.value).thenReturn(jacksonObjectMapper().writeValueAsString(message))
        whenever(assetService.get("snippets", message.snippetId)).thenReturn("some code")
        whenever(formatService.format(message.version, "some code", "rules")).thenReturn("formatted code")

        consumer.onMessage(record)
    }

    @Test
    fun `should handle formatting exception`() {
        val message = SnippetMessage(1L, 1L, "v1", "jwt-token")
        val record = mock<ObjectRecord<String, String>>()
        whenever(record.value).thenReturn(jacksonObjectMapper().writeValueAsString(message))
        whenever(assetService.get("snippets", message.snippetId)).thenThrow(RuntimeException("Error getting snippet"))

        consumer.onMessage(record)
    }
}
