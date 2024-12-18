package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType.NATIONAL_ID
import com.tnfigueiredo.tinybank.model.DocType.PASSPORT
import com.tnfigueiredo.tinybank.model.User
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.util.*

internal class UserRepositoryTest {

    private companion object{
        const val A_NAME = "A_NAME"
        const val A_SURNAME = "A_SURNAME"
        const val A_DOCUMENT = "A_DOCUMENT"
        const val A_DOC_COUNTRY = "A_DOC_COUNTRY"
        val A_RANDOM_ID: UUID = UUID.randomUUID()
    }

    private val underTest = UserRepository()

    @Test
    fun `when user is new and has no id`() {
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
}