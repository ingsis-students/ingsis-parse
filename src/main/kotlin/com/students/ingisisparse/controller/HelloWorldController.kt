package com.students.ingisisparse.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping("/hello")
    fun helloWorld(): String {
        return "Hello, World!"
    }
}
