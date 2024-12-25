package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType.PASSPORT
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.repositories.UserRepository
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@SpringBootTest
internal class UserServiceImplTest {

    private companion object{
        val aUser = User(UUID.randomUUID(), "A_NAME", "A_SURNAME", PASSPORT, "A_PASSPORT", "A_COUNTRY")
    }

    @MockitoBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserServiceImpl

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
    fun `when the repository fails due to data duplication`() {
        `when`(userRepository.saveOrUpdate(aUser)).thenReturn(Result.failure(DataDuplicatedException("Data duplicated")))
        val result = userService.createOrUpdateUser(aUser).exceptionOrNull()!!
        result.shouldBeInstanceOf<DataDuplicatedException>()
    }

    @Test
    fun `when there are mandatory fields missing`() {
        val result = userService.createOrUpdateUser(aUser.copy(name = "")).exceptionOrNull()!!
        result.shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when finding a user having document information`() {
        userRepository.saveOrUpdate(aUser)
        val result = userService.findUserByDocument(aUser.docType, aUser.document, aUser.docCountry)
        result.getOrNull()?.shouldBeEqual(aUser)
    }

    @Test
    fun `when there is no user for document information`() {
        val result = userService.findUserByDocument(aUser.docType, aUser.document, aUser.docCountry)
        result.getOrNull().shouldBeNull()
    }

    @Test
    fun `when deactivating an active user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.success(aUser))
        `when`(userRepository.deactivateUser(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))

        val result = userService.deactivateUser(aUser.id!!).getOrNull()

        result?.isUserActive()?.shouldBeFalse()
    }

    @Test
    fun `when deactivating a non active user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))
        userService.deactivateUser(aUser.id!!).exceptionOrNull().shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when activating a non active user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))
        `when`(userRepository.activateUser(aUser)).thenReturn(Result.success(aUser.activateUser()))

        val result = userService.activateUser(aUser).getOrNull()

        result?.isUserActive()?.shouldBeTrue()
    }

    @Test
    fun `when activating a non existing user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        userService.activateUser(aUser).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when activating a existing active user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.success(aUser))
        userService.activateUser(aUser).exceptionOrNull().shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when deactivating a non existing user`() {
        `when`(userRepository.findUserById(aUser.id!!)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        userService.deactivateUser(aUser.id!!).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }
}