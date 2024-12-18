package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType.PASSPORT
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.repositories.UserRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@SpringBootTest
internal class UserServiceTest {

    private companion object{
        val aUser = User(UUID.randomUUID(), "A_NAME", "A_SURNAME", PASSPORT, "A_PASSPORT", "A_COUNTRY")
    }

    @MockitoBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Test
    fun `when repository create a user successfully`() {
        `when`(userRepository.saveOrUpdate(aUser)).thenReturn(Result.success(aUser))
        val result = userService.createOrUpdateUser(aUser).getOrNull()
        result!! shouldBeEqual aUser
    }

    @Test
    fun `when the repository fails due to data not found`() {
        `when`(userRepository.saveOrUpdate(aUser)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        val result = userService.createOrUpdateUser(aUser).exceptionOrNull()!!
        result.shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when there are mandatory fields missing`() {
        val result = userService.createOrUpdateUser(aUser.copy(name = "")).exceptionOrNull()!!
        result.shouldBeInstanceOf<BusinessRuleValidationException>()
    }
}