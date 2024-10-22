package com.students.ingsisparse.linter.producers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service


interface LinterRuleProducer {
    suspend fun publishEvent(name: String)
}
@Service
class RedisLinterRuleProducer @Autowired constructor(
    @Value("\${stream.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : LinterRuleProducer, RedisStreamProducer(redis, streamKey) {
    override suspend fun publishEvent(name: String) {
        println("hola")
    }
}

