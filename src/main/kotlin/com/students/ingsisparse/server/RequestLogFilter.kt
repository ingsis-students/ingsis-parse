package com.students.ingsisparse.server

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.Filter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class RequestLogFilter : Filter {

    private val logger = LoggerFactory.getLogger(RequestLogFilter::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val uri = httpRequest.requestURI
        val method = httpRequest.method
        val prefix = "$method $uri"

        try {
            chain.doFilter(request, response)
        } finally {
            val statusCode = httpResponse.status
            logger.info("$prefix - $statusCode")
        }
    }
}
