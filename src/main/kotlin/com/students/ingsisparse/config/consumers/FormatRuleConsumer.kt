package com.students.ingsisparse.config.consumers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsisparse.asset.AssetService
import com.students.ingsisparse.config.SnippetMessage
import com.students.ingsisparse.formatter.FormatterService
import com.students.ingsisparse.snippet.SnippetService
import java.time.Duration
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class FormatRuleConsumer @Autowired constructor(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${stream.format.key}") streamKey: String,
    @Value("\${groups.format}") groupId: String,
    private val formatService: FormatterService,
    private val assetService: AssetService,
    private val snippetService: SnippetService,
) : RedisStreamConsumer<String>(streamKey, groupId, redisTemplate) {
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
        /**
         * how the consumer behaves
         * pollTimeout: time until checks for new messages
         * targetType: deserealize data into that type
         */
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(String::class.java)
            .build()
    }

    public override fun onMessage(record: ObjectRecord<String, String>) {
        val message: SnippetMessage = jacksonObjectMapper().readValue(record.value, SnippetMessage::class.java)
        try {
            val formatRules: String = getRulesAsString(message)
            val content = assetService.get("snippets", message.snippetId)
            val formattedCode = formatService.format(message.version, content, formatRules)
            assetService.put("snippets", message.snippetId, formattedCode)
        } catch (e: Exception) {
            println("Error formatting: ${e.message}")
        }
    }

    private fun getRulesAsString(message: SnippetMessage): String {
        return try {
            val formatRulesJson = assetService.get("format-rules", message.userId)
            formatService.getActiveAdaptedRules(formatRulesJson)
        } catch (e: Exception) {
            println("Error deserializing lint rules: ${e.message}")
            ""
        }
    }
}
