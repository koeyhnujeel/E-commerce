package com.zunza.commerce.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.commerce.security.JwtAccessDeniedHandler
import com.zunza.commerce.security.JwtAuthenticationEntryPoint
import com.zunza.commerce.security.JwtAuthenticationFilter
import com.zunza.commerce.security.JwtExceptionFilter
import com.zunza.commerce.security.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val jwtProvider: JwtProvider
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { authorize ->
                authorize
                    .anyRequest().permitAll()
            }

            .addFilterBefore(
                JwtExceptionFilter(objectMapper),
                UsernamePasswordAuthenticationFilter::class.java
            )

            .addFilterBefore(
                JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(JwtAuthenticationEntryPoint(objectMapper))
                    .accessDeniedHandler(JwtAccessDeniedHandler(objectMapper))
            }

        return http.build()
    }
}
