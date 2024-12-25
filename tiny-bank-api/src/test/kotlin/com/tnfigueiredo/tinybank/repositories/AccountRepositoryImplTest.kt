package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.exceptions.NoConsistentDataException
import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.model.ActivationStatus.ACTIVE
import com.tnfigueiredo.tinybank.model.ActivationStatus.DEACTIVATED
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AccountRepositoryImplTest {

    private companion object{
        const val A_ACCOUNT_ID = "202400560001"
        const val ANOTHER_ACCOUNT_ID = "202400550001"
        val A_RANDOM_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
        const val AN_ACCOUNT_PREFIX = "20240056"
    }

    private val underTest: AccountRepository = AccountRepositoryImpl()

    @BeforeEach
    fun setUp(){
        underTest.deleteAll()
    }

    @Test
    fun `when account has null id`() {
        val result = underTest.saveAccount(Account(userId = A_RANDOM_ID))
        result.exceptionOrNull().shouldBeInstanceOf<NoConsistentDataException>()
    }

    @Test
    fun `when the account id already exists`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID)
        underTest.saveAccount(account)
        underTest.saveAccount(account).exceptionOrNull().shouldBeInstanceOf<DataDuplicatedException>()
    }

    @Test
    fun `when account data is correct to be saved`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID)
        val result = underTest.saveAccount(account)

        result.getOrNull()?.id?.shouldBeEqual(account.id!!)
        result.getOrNull()?.userId?.shouldBeEqual(account.userId)
        result.getOrNull()?.balance?.shouldBeEqual(0.0)
        result.getOrNull()?.status?.shouldBeEqual(ACTIVE)
    }

    @Test
    fun `get next account when there is not account`() {
        underTest.findLatestAccount(AN_ACCOUNT_PREFIX).shouldBeNull()
    }

    @Test
    fun `get next account number when there is no account for this year in the agency`() {
        val account = Account(id = ANOTHER_ACCOUNT_ID, userId = A_RANDOM_ID)
        underTest.saveAccount(account)
        underTest.findLatestAccount(AN_ACCOUNT_PREFIX).shouldBeNull()
    }

    @Test
    fun `get an account number when there is an account for an agency in a year`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID)
        underTest.saveAccount(account)
        underTest.findLatestAccount(AN_ACCOUNT_PREFIX)?.shouldBeEqual(A_ACCOUNT_ID)
    }

    @Test
    fun `when getting an account by id that exists`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID, balance = 100.0)
        underTest.saveAccount(account)
        val result = underTest.getAccountById(A_ACCOUNT_ID)

        result.getOrNull()?.id?.shouldBeEqual(account.id!!)
        result.getOrNull()?.userId?.shouldBeEqual(account.userId)
        result.getOrNull()?.balance?.shouldBeEqual(account.balance)
        result.getOrNull()?.status?.shouldBeEqual(account.status)
    }

    @Test
    fun `when getting an account by id that doesn't exist`() {
        underTest.getAccountById(A_ACCOUNT_ID).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when getting an account by user id for a user having account`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID, balance = 100.0)
        underTest.saveAccount(account)
        val result = underTest.getAccountByUserId(A_RANDOM_ID)

        result.getOrNull()?.id?.shouldBeEqual(account.id!!)
        result.getOrNull()?.userId?.shouldBeEqual(account.userId)
        result.getOrNull()?.balance?.shouldBeEqual(account.balance)
        result.getOrNull()?.status?.shouldBeEqual(account.status)
    }

    @Test
    fun `when getting an account by user id for a user having no account`() {
        underTest.getAccountByUserId(A_RANDOM_ID).getOrNull().shouldBeNull()
    }

    @Test
    fun `when deactivate an existing account successfully`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID)
        underTest.saveAccount(account)
        val result = underTest.deactivateAccount(A_ACCOUNT_ID)
        result.getOrNull()?.isAccountActive()?.shouldBeFalse()
    }

    @Test
    fun `when deactivating a non existing account`() {
        underTest.deactivateAccount(A_ACCOUNT_ID).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }

    @Test
    fun `when activating an existing account successfully`() {
        val account = Account(id = A_ACCOUNT_ID, userId = A_RANDOM_ID, status = DEACTIVATED)
        underTest.saveAccount(account)
        val result = underTest.activateAccount(A_ACCOUNT_ID)
        result.getOrNull()?.isAccountActive()?.shouldBeEqual(true)
    }

    @Test
    fun `when activating a non existing account`() {
        underTest.activateAccount(A_ACCOUNT_ID).exceptionOrNull().shouldBeInstanceOf<DataNotFoundException>()
    }
}