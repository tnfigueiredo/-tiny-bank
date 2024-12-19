package com.tnfigueiredo.tinybank.config

import com.tnfigueiredo.tinybank.services.UserService
import com.tnfigueiredo.tinybank.stubs.UserServiceStub
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
class TestConfiguration {

    @Bean
    fun userService(): UserService = UserServiceStub()

}