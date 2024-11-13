package com.students.ingsisparse.config
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsisparse.asset.AssetService
import com.students.ingsisparse.config.consumers.LinterRuleConsumer
import com.students.ingsisparse.linter.LinterService
import com.students.ingsisparse.snippet.SnippetService
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class LinterRuleConsumerTest {

    private val redisTemplate = mock<ReactiveRedisTemplate<String, String>>()
    private val lintService = mock<LinterService>()
    private val assetService = mock<AssetService>()
    private val snippetService = mock<SnippetService>()
    private val consumer = LinterRuleConsumer(
        redisTemplate,
        "testStreamKey",
        "testGroupId",
        lintService,
        assetService,
        snippetService
    )

    @Test
    fun `should lint code successfully`() {
        val message = SnippetMessage(1L, 1L, "v1", "jwt-token")
        val record = mock<ObjectRecord<String, String>>()
        whenever(record.value).thenReturn(jacksonObjectMapper().writeValueAsString(message))
        whenever(assetService.get("snippets", message.snippetId)).thenReturn("some code")
        whenever(assetService.get("lint-rules", message.userId)).thenReturn("[{\"rule\":\"no-tabs\"}]")
        whenever(lintService.analyze(eq(message.version), eq("some code"), any())).thenReturn(emptyList())
        whenever(lintService.convertActiveRulesToJsonObject(any())).thenReturn(JsonObject(emptyMap()))

        consumer.onMessage(record)
    }

    @Test
    fun `should handle error during linting`() {
        val message = SnippetMessage(1L, 1L, "v1", "jwt-token")
        val record = mock<ObjectRecord<String, String>>()
        whenever(record.value).thenReturn(jacksonObjectMapper().writeValueAsString(message))
        whenever(assetService.get("snippets", message.snippetId)).thenReturn("some code")
        whenever(assetService.get("lint-rules", message.userId)).thenThrow(RuntimeException("Error getting lint rules"))
        consumer.onMessage(record)
    }
}
