package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.model.TransactionType.DEPOSIT
import com.tnfigueiredo.tinybank.model.TransactionType.WITHDRAW
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class TransactionRepositoryImplTest {

    private val underTest: TransactionRepository = TransactionRepositoryImpl()

    @Test
    fun `when it is requested to save a first transaction`() {
        val transaction = Transaction(originAccountId = "202400560001", destinationAccountId = "202400550001", amount = 100.0, type = DEPOSIT, accountBalanceCurrentValue = 100.0)
        val result = underTest.saveTransaction(transaction).getOrNull()

        result?.id.shouldNotBeNull()
        result?.originAccountId?.shouldBeEqual(transaction.originAccountId)
        result?.destinationAccountId.equals(transaction.destinationAccountId).shouldBeTrue()
        result?.amount?.shouldBeEqual(transaction.amount)
        result?.type?.shouldBeEqual(transaction.type)
        result?.accountBalanceCurrentValue?.shouldBeEqual(transaction.accountBalanceCurrentValue)
    }

    @Test
    fun `when it is being saved a second transaction for the same account`() {
        val firstTransaction = Transaction(originAccountId = "202400560001", amount = 100.0, type = DEPOSIT, accountBalanceCurrentValue = 100.0)
        val secondTransaction = Transaction(originAccountId = "202400560001", amount = 50.0, type = WITHDRAW, accountBalanceCurrentValue = 50.0)

        underTest.saveTransaction(firstTransaction)
        val result = underTest.saveTransaction(secondTransaction).getOrNull()

        result?.id.shouldNotBeNull()
        result?.originAccountId?.shouldBeEqual(secondTransaction.originAccountId)
        result?.destinationAccountId.equals(secondTransaction.destinationAccountId).shouldBeTrue()
        result?.amount?.shouldBeEqual(secondTransaction.amount)
        result?.type?.shouldBeEqual(secondTransaction.type)
        result?.accountBalanceCurrentValue?.shouldBeEqual(secondTransaction.accountBalanceCurrentValue)
    }

    @Test
    fun `when it is being saved a second transaction for a different account`() {
        val firstTransaction = Transaction(originAccountId = "202400560001", amount = 100.0, type = DEPOSIT, accountBalanceCurrentValue = 100.0)
        val secondTransaction = Transaction(originAccountId = "202400560002", amount = 50.0, type = WITHDRAW, accountBalanceCurrentValue = 150.0)

        val firstTransactionResult = underTest.saveTransaction(firstTransaction)
        val secondTransactionResult = underTest.saveTransaction(secondTransaction).getOrNull()

        firstTransactionResult.getOrNull()?.id.shouldNotBeNull()
        firstTransactionResult.getOrNull()?.originAccountId?.shouldBeEqual(firstTransaction.originAccountId)
        firstTransactionResult.getOrNull()?.destinationAccountId.equals(firstTransaction.destinationAccountId).shouldBeTrue()
        firstTransactionResult.getOrNull()?.amount?.shouldBeEqual(firstTransaction.amount)
        firstTransactionResult.getOrNull()?.type?.shouldBeEqual(firstTransaction.type)
        firstTransactionResult.getOrNull()?.accountBalanceCurrentValue?.shouldBeEqual(firstTransaction.accountBalanceCurrentValue)

        secondTransactionResult?.id.shouldNotBeNull()
        secondTransactionResult?.originAccountId?.shouldBeEqual(secondTransaction.originAccountId)
        secondTransactionResult?.destinationAccountId.equals(secondTransaction.destinationAccountId).shouldBeTrue()
        secondTransactionResult?.amount?.shouldBeEqual(secondTransaction.amount)
        secondTransactionResult?.type?.shouldBeEqual(secondTransaction.type)
        secondTransactionResult?.accountBalanceCurrentValue?.shouldBeEqual(secondTransaction.accountBalanceCurrentValue)
    }
}