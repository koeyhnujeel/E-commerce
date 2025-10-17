package com.zunza.commerce.config

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {

    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> =
        MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withReuse(true)

    @Bean
    @ServiceConnection
    fun redisContainer(): RedisContainer =
        RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)
}
