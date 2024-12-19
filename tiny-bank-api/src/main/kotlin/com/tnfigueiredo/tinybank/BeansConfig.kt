package com.tnfigueiredo.tinybank

import com.tnfigueiredo.tinybank.repositories.UserRepository
import com.tnfigueiredo.tinybank.repositories.UserRepositoryImpl
import com.tnfigueiredo.tinybank.services.UserService
import com.tnfigueiredo.tinybank.services.UserServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeansConfig {

    @Bean
    fun userRepository(): UserRepository = UserRepositoryImpl()

    @Bean
    fun userService(userRepository: UserRepository): UserService = UserServiceImpl(userRepository)

}