package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.model.TransactionType.DEPOSIT
import com.tnfigueiredo.tinybank.model.TransactionType.WITHDRAW
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

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

    @Test
    fun `when filtering transactions inside a time range`() {
        val firstTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 100.0,
            type = DEPOSIT,
            accountBalanceCurrentValue = 100.0,
            date = LocalDateTime.parse("2021-01-01T00:00:00")
        )
        val secondTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 50.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 50.0,
            date = LocalDateTime.parse("2021-01-02T00:00:00")
        )
        val thirdTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 25.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 25.0,
            date = LocalDateTime.parse("2021-01-03T00:00:00")
        )

        underTest.saveTransaction(firstTransaction)
        underTest.saveTransaction(secondTransaction)
        underTest.saveTransaction(thirdTransaction)

        val result = underTest.getTransactionsByDateRange(
            "202400560001",
            LocalDateTime.parse("2021-01-01T00:00:00"),
            LocalDateTime.parse("2021-01-03T00:00:00")
        ).getOrNull()

        result?.size?.shouldBeEqual(3)

        firstTransaction.originAccountId shouldBeEqual result!![0].originAccountId
        firstTransaction.amount shouldBeEqual result[0].amount
        firstTransaction.type shouldBeEqual result[0].type
        firstTransaction.accountBalanceCurrentValue shouldBeEqual result[0].accountBalanceCurrentValue
        firstTransaction.date shouldBeEqual result[0].date

        secondTransaction.originAccountId shouldBeEqual result[1].originAccountId
        secondTransaction.amount shouldBeEqual result[1].amount
        secondTransaction.type shouldBeEqual result[1].type
        secondTransaction.accountBalanceCurrentValue shouldBeEqual result[1].accountBalanceCurrentValue
        secondTransaction.date shouldBeEqual result[1].date

        thirdTransaction.originAccountId shouldBeEqual result[2].originAccountId
        thirdTransaction.amount shouldBeEqual result[2].amount
        thirdTransaction.type shouldBeEqual result[2].type
        thirdTransaction.accountBalanceCurrentValue shouldBeEqual result[2].accountBalanceCurrentValue
        thirdTransaction.date shouldBeEqual result[2].date

    }

    @Test
    fun `when filtering transactions having a transaction out of the date range`() {
        val firstTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 100.0,
            type = DEPOSIT,
            accountBalanceCurrentValue = 100.0,
            date = LocalDateTime.parse("2021-01-01T00:00:00")
        )
        val secondTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 50.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 50.0,
            date = LocalDateTime.parse("2021-01-02T00:00:00")
        )
        val thirdTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 25.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 25.0,
            date = LocalDateTime.parse("2021-01-05T00:00:00")
        )

        underTest.saveTransaction(firstTransaction)
        underTest.saveTransaction(secondTransaction)
        underTest.saveTransaction(thirdTransaction)

        val result = underTest.getTransactionsByDateRange(
            "202400560001",
            LocalDateTime.parse("2021-01-01T00:00:00"),
            LocalDateTime.parse("2021-01-03T00:00:00")
        ).getOrNull()

        result?.size?.shouldBeEqual(2)

        firstTransaction.originAccountId shouldBeEqual result!![0].originAccountId
        firstTransaction.amount shouldBeEqual result[0].amount
        firstTransaction.type shouldBeEqual result[0].type
        firstTransaction.accountBalanceCurrentValue shouldBeEqual result[0].accountBalanceCurrentValue
        firstTransaction.date shouldBeEqual result[0].date

        secondTransaction.originAccountId shouldBeEqual result[1].originAccountId
        secondTransaction.amount shouldBeEqual result[1].amount
        secondTransaction.type shouldBeEqual result[1].type
        secondTransaction.accountBalanceCurrentValue shouldBeEqual result[1].accountBalanceCurrentValue
        secondTransaction.date shouldBeEqual result[1].date

    }

    @Test
    fun `when filtering transactions having a transaction from another account`() {
        val firstTransaction = Transaction(
            originAccountId = "202400570001",
            amount = 100.0,
            type = DEPOSIT,
            accountBalanceCurrentValue = 100.0,
            date = LocalDateTime.parse("2021-01-01T00:00:00")
        )
        val secondTransaction = Transaction(
            originAccountId = "202400570002",
            amount = 50.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 50.0,
            date = LocalDateTime.parse("2021-01-02T00:00:00")
        )
        val thirdTransaction = Transaction(
            originAccountId = "202400570001",
            amount = 25.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 25.0,
            date = LocalDateTime.parse("2021-01-03T00:00:00")
        )

        underTest.saveTransaction(firstTransaction)
        underTest.saveTransaction(secondTransaction)
        underTest.saveTransaction(thirdTransaction)

        val result = underTest.getTransactionsByDateRange(
            "202400570001",
            LocalDateTime.parse("2021-01-01T00:00:00"),
            LocalDateTime.parse("2021-01-03T00:00:00")
        ).getOrNull()

        result?.size?.shouldBeEqual(2)

        firstTransaction.originAccountId shouldBeEqual result!![0].originAccountId
        firstTransaction.amount shouldBeEqual result[0].amount
        firstTransaction.type shouldBeEqual result[0].type
        firstTransaction.accountBalanceCurrentValue shouldBeEqual result[0].accountBalanceCurrentValue
        firstTransaction.date shouldBeEqual result[0].date

        thirdTransaction.originAccountId shouldBeEqual result[1].originAccountId
        thirdTransaction.amount shouldBeEqual result[1].amount
        thirdTransaction.type shouldBeEqual result[1].type
        thirdTransaction.accountBalanceCurrentValue shouldBeEqual result[1].accountBalanceCurrentValue
        thirdTransaction.date shouldBeEqual result[1].date
    }

    @Test
    fun `when filtering a date range with no transactions`() {
        val firstTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 100.0,
            type = DEPOSIT,
            accountBalanceCurrentValue = 100.0,
            date = LocalDateTime.parse("2021-01-01T00:00:00")
        )
        val secondTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 50.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 50.0,
            date = LocalDateTime.parse("2021-01-02T00:00:00")
        )
        val thirdTransaction = Transaction(
            originAccountId = "202400560001",
            amount = 25.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 25.0,
            date = LocalDateTime.parse("2021-01-05T00:00:00")
        )

        underTest.saveTransaction(firstTransaction)
        underTest.saveTransaction(secondTransaction)
        underTest.saveTransaction(thirdTransaction)

        underTest.getTransactionsByDateRange(
            "202400570001",
            LocalDateTime.parse("2021-01-06T00:00:00"),
            LocalDateTime.parse("2021-01-07T00:00:00")
        ).getOrNull().shouldBeEmpty()
    }
}