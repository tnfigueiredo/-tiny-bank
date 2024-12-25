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
    lateinit var userRepositoryMock: UserRepository

    @Autowired
    lateinit var underTest: UserServiceImpl

    @Test
    fun `when repository create a user successfully`() {
        `when`(userRepositoryMock.saveOrUpdate(aUser)).thenReturn(Result.success(aUser))
        val result = underTest.createOrUpdateUser(aUser).getOrNull()
        result!! shouldBeEqual aUser
    }

    @Test
    fun `when the repository fails due to data not found`() {
        `when`(userRepositoryMock.saveOrUpdate(aUser)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        val result = underTest.createOrUpdateUser(aUser).exceptionOrNull()!!
        result.shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when the repository fails due to data duplication`() {
        `when`(userRepositoryMock.saveOrUpdate(aUser)).thenReturn(Result.failure(DataDuplicatedException("Data duplicated")))
        val result = underTest.createOrUpdateUser(aUser).exceptionOrNull()!!
        result.shouldBeInstanceOf<DataDuplicatedException>()
    }

    @Test
    fun `when there are mandatory fields missing`() {
        val result = underTest.createOrUpdateUser(aUser.copy(name = "")).exceptionOrNull()!!
        result.shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when finding a user having document information`() {
        userRepositoryMock.saveOrUpdate(aUser)
        val result = underTest.findUserByDocument(aUser.docType, aUser.document, aUser.docCountry)
        result.getOrNull()?.shouldBeEqual(aUser)
    }

    @Test
    fun `when there is no user for document information`() {
        val result = underTest.findUserByDocument(aUser.docType, aUser.document, aUser.docCountry)
        result.getOrNull().shouldBeNull()
    }

    @Test
    fun `when deactivating an active user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.success(aUser))
        `when`(userRepositoryMock.deactivateUser(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))

        val result = underTest.deactivateUser(aUser.id!!).getOrNull()

        result?.isUserActive()?.shouldBeFalse()
    }

    @Test
    fun `when deactivating a non active user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))
        underTest.deactivateUser(aUser.id!!).exceptionOrNull().shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when activating a non active user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.success(aUser.deactivateUser()))
        `when`(userRepositoryMock.activateUser(aUser)).thenReturn(Result.success(aUser.activateUser()))

        val result = underTest.activateUser(aUser).getOrNull()

        result?.isUserActive()?.shouldBeTrue()
    }

    @Test
    fun `when activating a non existing user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        underTest.activateUser(aUser).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when activating a existing active user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.success(aUser))
        underTest.activateUser(aUser).exceptionOrNull().shouldBeInstanceOf<BusinessRuleValidationException>()
    }

    @Test
    fun `when deactivating a non existing user`() {
        `when`(userRepositoryMock.findUserById(aUser.id!!)).thenReturn(Result.failure(DataNotFoundException("Data not found")))
        underTest.deactivateUser(aUser.id!!).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }
}