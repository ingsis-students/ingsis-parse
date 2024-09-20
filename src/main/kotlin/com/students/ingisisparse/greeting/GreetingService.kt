package com.students.ingisisparse.greeting

import org.springframework.stereotype.Service

@Service
class GreetingService {
    fun greet(): String {
        return "Hello, World!"
    }
}
