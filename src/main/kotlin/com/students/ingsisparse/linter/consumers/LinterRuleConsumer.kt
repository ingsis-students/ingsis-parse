package com.students.ingsisparse.linter.consumers

import com.students.ingsisparse.linter.LintDto
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Service
import java.time.Duration
import org.springframework.data.redis.connection.stream.ObjectRecord

@Service
@Profile("!test")
class LintRuleConsumer @Autowired constructor(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${stream.lint.key}") streamKey: String,
    @Value("\${groups.lint}") groupId: String
) : RedisStreamConsumer<LintDto>(streamKey, groupId, redisTemplate) {


    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, LintDto>> {
        /**
         * how the consumer behaves
         * pollTimeout: time until checks for new messages
         * targetType: deserealize data into that type
         */
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000))
            .targetType(LintDto::class.java)
            .build()
    }

    override fun onMessage(record: ObjectRecord<String, LintDto>) {
        // Process the linting rule asynchronously
        println("Processing linting rule: ${record.value}")
        // Add logic to apply the new rule to snippets
    }
}
