package com.students.ingisisparse.greeting

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetingController(private val greetingService: GreetingService) {

    @GetMapping("/greeting")
    fun helloWorld(): String {
        return greetingService.greet()
    }
}
