package com.students.ingisisparse.interpreter

import org.printers.Printer

class HttpPrinter : Printer {
    val prints = mutableListOf<String>()
    override fun print(message: String) {
        prints.add(message)
    }
}
