package com.students.ingsisparse.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory


@Configuration
class ConnectionFactory(@Value("\${spring.data.redis.host}") private val hostName: String,
                        @Value("\${spring.data.redis.port}") private val port: Int) {
    @Bean // method called when app starts and added to spring app context. (able to be used anywhere)
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(hostName, port))
    }
}