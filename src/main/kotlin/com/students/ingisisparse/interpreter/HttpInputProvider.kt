package com.students.ingisisparse.interpreter

import org.inputers.InputProvider
import java.util.Queue

class HttpInputProvider(private val queue: Queue<String>) : InputProvider {
    override fun input(): String {
        return queue.poll()
    }
}
