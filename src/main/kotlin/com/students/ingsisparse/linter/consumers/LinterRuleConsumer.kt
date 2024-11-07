package com.students.ingsisparse.linter.consumers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsisparse.asset.AssetService
import com.students.ingsisparse.config.SnippetMessage
import com.students.ingsisparse.linter.LinterService
import com.students.ingsisparse.types.Rule
import java.time.Duration
import kotlinx.serialization.json.JsonObject
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
class LinterRuleConsumer @Autowired constructor(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${stream.lint.key}") streamKey: String,
    @Value("\${groups.lint}") groupId: String,
    private val lintService: LinterService,
    private val assetService: AssetService
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

    override fun onMessage(record: ObjectRecord<String, String>) {
        try {
            println("starting liniting asyncronically")
            val message: SnippetMessage = jacksonObjectMapper().readValue(record.value, SnippetMessage::class.java)
            val lintRules: JsonObject = getRulesAsJsonObject(message)
            println("lintRules $lintRules")
            val content = assetService.get("snippets", message.snippetId)
            println("content of snippet $content")
            lintService.analyze("1.1", content, lintRules)
            println("Successfully linted: ${record.id}")
        } catch (e: Exception) {
            println("Error linting: ${e.message}")
        }
    }

    private fun getRulesAsJsonObject(message: SnippetMessage): JsonObject {
        return try {
            println("getting rules from asset service")
            val lintRulesJson = assetService.get("lint-rules", message.userId)
            println("rules gotten at consumer: $lintRulesJson")
            val objectMapper = jacksonObjectMapper()
            val lintRules: List<Rule> = objectMapper.readValue(lintRulesJson, object : TypeReference<List<Rule>>() {})
            lintService.convertActiveRulesToJsonObject(lintRules)
        } catch (e: Exception) {
            println("Error deserializing lint rules: ${e.message}")
            JsonObject(emptyMap())
        }
    }
}
