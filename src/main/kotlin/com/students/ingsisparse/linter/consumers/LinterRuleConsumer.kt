package com.students.ingsisparse.linter.consumers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
) : RedisStreamConsumer<SnippetMessage>(streamKey, groupId, redisTemplate) {
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, SnippetMessage>> {
        /**
         * how the consumer behaves
         * pollTimeout: time until checks for new messages
         * targetType: deserealize data into that type
         */
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(SnippetMessage::class.java)
            .build()
    }

    override fun onMessage(record: ObjectRecord<String, SnippetMessage>) {
        val message = record.value
        val lintRules: JsonObject = getRulesAsJsonObject(message)
        val content = assetService.get("snippets", message.snippetId)
        lintService.analyze("1.1", content, lintRules)
    }

    private fun getRulesAsJsonObject(message: SnippetMessage): JsonObject {
        val lintRulesJson = assetService.get("lint-rules", message.userId)
        val objectMapper = ObjectMapper()
        val lintRules: List<Rule> = objectMapper.readValue(lintRulesJson, object : TypeReference<List<Rule>>() {})
        return lintService.convertActiveRulesToJsonObject(lintRules)
    }
}
