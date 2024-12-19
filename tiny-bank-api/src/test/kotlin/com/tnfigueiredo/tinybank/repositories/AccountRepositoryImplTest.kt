package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.NoConsistentDataException
import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.model.ActivationStatus.ACTIVE
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
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
        result.getOrNull()?.balance?.shouldBeEqual("0.0".toDouble())
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
}