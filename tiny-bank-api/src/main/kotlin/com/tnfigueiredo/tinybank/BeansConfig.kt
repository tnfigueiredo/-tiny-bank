package com.tnfigueiredo.tinybank

import com.tnfigueiredo.tinybank.repositories.*
import com.tnfigueiredo.tinybank.services.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeansConfig {

    @Bean
    fun userRepository(): UserRepository = UserRepositoryImpl()

    @Bean
    fun accountRepository(): AccountRepository = AccountRepositoryImpl()

    @Bean
    fun transactionRepository(): TransactionRepository = TransactionRepositoryImpl()

    @Bean
    fun userService(userRepository: UserRepository): UserService = UserServiceImpl(userRepository)

    @Bean
    fun accountService(accountRepository: AccountRepository): AccountService = AccountServiceImpl(accountRepository)

    @Bean
    fun transactionService(transactionRepository: TransactionRepository): TransactionService = TransactionServiceImpl(transactionRepository)

}