package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType.NATIONAL_ID
import com.tnfigueiredo.tinybank.model.DocType.PASSPORT
import com.tnfigueiredo.tinybank.model.User
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class UserRepositoryImplTest {

    private companion object{
        const val A_NAME = "A_NAME"
        const val ANOTHER_NAME = "ANOTHER_NAME"
        const val A_SURNAME = "A_SURNAME"
        const val ANOTHER_SURNAME = "ANOTHER_SURNAME"
        const val A_DOCUMENT = "A_DOCUMENT"
        const val A_DOC_COUNTRY = "A_DOC_COUNTRY"
        val A_RANDOM_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
    }

    private val underTest:UserRepository = UserRepositoryImpl()

    @BeforeEach
    fun setUp(){
        underTest.deleteAll()
    }

    @Test
    fun `when user is new and has no id it is created successfully`() {
        val userToBeSaved = User(name = A_NAME, surname = A_SURNAME, docType = NATIONAL_ID, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        val savedUser = underTest.saveOrUpdate(userToBeSaved).getOrNull()

        savedUser?.id.shouldNotBeNull()
        savedUser?.name?.shouldBeEqual(A_NAME)
        savedUser?.surname?.shouldBeEqual(A_SURNAME)
        savedUser?.docType?.shouldBeEqual(NATIONAL_ID)
        savedUser?.document?.shouldBeEqual(A_DOCUMENT)
        savedUser?.docCountry?.shouldBeEqual(A_DOC_COUNTRY)
    }

    @Test
    fun `when new user has duplicated document`() {
        val userToBeSaved = User(name = A_NAME, surname = A_SURNAME, docType = NATIONAL_ID, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        underTest.saveOrUpdate(userToBeSaved)
        underTest.saveOrUpdate(userToBeSaved).exceptionOrNull().shouldBeInstanceOf<DataDuplicatedException>()
    }

    @Test
    fun `when user to be updated is not found in the user repository`() {
        val userToBeSaved = User(id = A_RANDOM_ID, name = A_NAME, surname = A_SURNAME, docType = NATIONAL_ID, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        underTest.saveOrUpdate(userToBeSaved).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when updating an existing user`() {
        val existingUser = User(id = A_RANDOM_ID, name = A_NAME, surname = A_SURNAME, docType = NATIONAL_ID, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        underTest.saveOrUpdate(existingUser)
        val userToBeSaved = User(id = A_RANDOM_ID, name = A_NAME, surname = A_SURNAME, docType = PASSPORT, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        val savedUser = underTest.saveOrUpdate(userToBeSaved).getOrNull()

        savedUser?.id?.shouldBeEqual(A_RANDOM_ID)
        savedUser?.name?.shouldBeEqual(A_NAME)
        savedUser?.surname?.shouldBeEqual(A_SURNAME)
        savedUser?.docType?.shouldBeEqual(PASSPORT)
        savedUser?.document?.shouldBeEqual(A_DOCUMENT)
        savedUser?.docCountry?.shouldBeEqual(A_DOC_COUNTRY)
    }

    @Test
    fun `when user to be updated has duplicated document from other user`() {
        val existingUser = User(name = ANOTHER_NAME, surname = ANOTHER_SURNAME, docType = NATIONAL_ID, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        val userToBeSaved = User(name = A_NAME, surname = A_SURNAME, docType = PASSPORT, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        underTest.saveOrUpdate(existingUser)
        val secondResult = underTest.saveOrUpdate(userToBeSaved).getOrNull()!!
        underTest.saveOrUpdate(userToBeSaved.copy(id = secondResult.id, docType = NATIONAL_ID)).exceptionOrNull().shouldBeInstanceOf<DataDuplicatedException>()
    }

    @Test
    fun `when deleting all users successfully`() {
        val userToBeSaved = User(name = A_NAME, surname = A_SURNAME, docType = PASSPORT, document = A_DOCUMENT, docCountry = A_DOC_COUNTRY)
        underTest.saveOrUpdate(userToBeSaved)
        underTest.saveOrUpdate(userToBeSaved).exceptionOrNull().shouldBeInstanceOf<DataDuplicatedException>()
        underTest.deleteAll()
        val savedUser = underTest.saveOrUpdate(userToBeSaved).getOrNull()

        savedUser?.id.shouldNotBeNull()
        savedUser?.name?.shouldBeEqual(A_NAME)
        savedUser?.surname?.shouldBeEqual(A_SURNAME)
        savedUser?.docType?.shouldBeEqual(PASSPORT)
        savedUser?.document?.shouldBeEqual(A_DOCUMENT)
        savedUser?.docCountry?.shouldBeEqual(A_DOC_COUNTRY)

    }
}