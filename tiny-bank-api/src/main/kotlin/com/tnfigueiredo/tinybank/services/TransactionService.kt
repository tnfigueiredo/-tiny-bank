package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.TransactionDeniedException
import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.model.TransactionType.*
import com.tnfigueiredo.tinybank.repositories.TransactionRepository

interface TransactionService {

    fun saveTransaction(transaction: Transaction): Result<Transaction>

}

class TransactionServiceImpl(private val transactionRepository: TransactionRepository) : TransactionService {

    override fun saveTransaction(transaction: Transaction): Result<Transaction> = kotlin.runCatching {
        when(transaction.type){
            DEPOSIT -> deposit(transaction)
            WITHDRAW -> withdraw(transaction)
            TRANSFER -> transfer(transaction)
            TRANSFER_DEBIT -> throw TransactionDeniedException("Transfer debits is for internal use only")
        }
    }


    private fun deposit(transaction: Transaction): Transaction =
        if(transaction.amount > 0) {
            transactionRepository.saveTransaction(transaction.copy(accountBalanceCurrentValue = transaction.accountBalanceCurrentValue + transaction.amount)).getOrNull()!!
        } else {
            throw TransactionDeniedException("Deposit amount must be greater than 0")
        }

    private fun withdraw(transaction: Transaction): Transaction =
        when{
            transaction.accountBalanceCurrentValue + transaction.amount < 0 -> throw TransactionDeniedException("Withdraw amount makes account balance negative.")
            transaction.amount < 0 -> transactionRepository.saveTransaction(transaction.copy(accountBalanceCurrentValue = transaction.accountBalanceCurrentValue + transaction.amount)).getOrNull()!!
            else -> throw TransactionDeniedException("Withdraw amount must be lower than 0")
        }

    private fun transfer(transaction: Transaction): Transaction =
        when{
            transaction.destinationAccountId == null -> throw TransactionDeniedException("Transfer must have a destination account.")
            transaction.amount < 0 -> throw TransactionDeniedException("Transfer amount must be greater than 0")
            transaction.accountDestinationBalanceCurrentValue - transaction.amount < 0 -> throw TransactionDeniedException("Transfer can't make destination account negative.")
            transaction.amount > 0 -> {
                transactionRepository.saveTransaction(
                    transaction.copy(
                        originAccountId = transaction.destinationAccountId,
                        destinationAccountId = transaction.originAccountId,
                        accountBalanceCurrentValue = transaction.accountDestinationBalanceCurrentValue - transaction.amount,
                        accountDestinationBalanceCurrentValue = transaction.accountBalanceCurrentValue + transaction.amount,
                        type = TRANSFER_DEBIT
                    ))
                transactionRepository.saveTransaction(
                    transaction.copy(
                        accountBalanceCurrentValue = transaction.accountBalanceCurrentValue + transaction.amount,
                        accountDestinationBalanceCurrentValue = transaction.accountDestinationBalanceCurrentValue - transaction.amount
                    )
                ).getOrNull()!!
            }
            else -> throw TransactionDeniedException("Transfer amount denied for unknown reason.")
        }

}