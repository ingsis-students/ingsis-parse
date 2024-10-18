package com.students.ingsisparse.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class OAuth2ResourceServerSecurityConfiguration(
    @Value(
        "\${auth0.audience}"
    )
    val audience: String,
    @Value(
        "\${spring.security.oauth2.resourceserver.jwt.issuer-uri}"
    )
    val issuer: String
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it
                .requestMatchers("/").permitAll()
                .requestMatchers(POST, "/api/printscript/interpret").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(POST, "/api/printscript/test").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(POST, "/api/printscript/validate").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(POST, "/api/printscript/format").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/api/printscript/analyze").hasAuthority("SCOPE_write:snippets")
                .anyRequest().authenticated()
        }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
            .cors { it.disable() }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build()
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}
