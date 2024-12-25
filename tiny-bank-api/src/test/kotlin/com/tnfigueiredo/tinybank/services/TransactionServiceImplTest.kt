package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.TransactionDeniedException
import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.model.TransactionType.DEPOSIT
import com.tnfigueiredo.tinybank.model.TransactionType.WITHDRAW
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class TransactionServiceImplTest {

    @Autowired
    lateinit var underTest: TransactionServiceImpl

    @Test
    fun `when a deposit with positive value is done`() {
        val transaction = Transaction(
            originAccountId = "123",
            amount = 10.0,
            type = DEPOSIT
        )

        val result = underTest.saveTransaction(transaction)

        result.getOrNull()!!.accountBalanceCurrentValue shouldBe 10.0
        result.getOrNull()!!.id.shouldNotBeNull()
    }

    @Test
    fun `when a deposit of negative value is done`() {
        val transaction = Transaction(
            originAccountId = "123",
            amount = -10.0,
            type = DEPOSIT
        )

        val result = underTest.saveTransaction(transaction)

        result.exceptionOrNull().shouldBeInstanceOf<TransactionDeniedException>()
    }

    @Test
    fun `when a deposit of value zero is done`() {
        val transaction = Transaction(
            originAccountId = "123",
            amount = 0.0,
            type = DEPOSIT
        )

        val result = underTest.saveTransaction(transaction)

        result.exceptionOrNull().shouldBeInstanceOf<TransactionDeniedException>()
    }

    @Test
    fun `when withdrawal ok`() {
        val transaction = Transaction(
            originAccountId = "456",
            amount = -10.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 20.0
        )

        val result = underTest.saveTransaction(transaction)

        result.getOrNull()!!.accountBalanceCurrentValue shouldBe 10.0
        result.getOrNull()!!.id.shouldNotBeNull()

    }

    @Test
    fun `when withdrawal has zero amount`() {
        val transaction = Transaction(
            originAccountId = "456",
            amount = 0.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 20.0
        )

        underTest.saveTransaction(transaction).exceptionOrNull().shouldBeInstanceOf<TransactionDeniedException>()
    }

    @Test
    fun `when withdrawal is greater than zero`() {
        val transaction = Transaction(
            originAccountId = "456",
            amount = 10.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 20.0
        )

        underTest.saveTransaction(transaction).exceptionOrNull().shouldBeInstanceOf<TransactionDeniedException>()
    }

    @Test
    fun `when withdrawal makes account balance negative`() {
        val transaction = Transaction(
            originAccountId = "456",
            amount = -50.0,
            type = WITHDRAW,
            accountBalanceCurrentValue = 20.0
        )

        underTest.saveTransaction(transaction).exceptionOrNull().shouldBeInstanceOf<TransactionDeniedException>()
    }
}