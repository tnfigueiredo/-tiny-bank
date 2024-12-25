package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.model.Transaction
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayDeque

interface TransactionRepository{

    fun saveTransaction(transaction: Transaction): Result<Transaction>

}

class TransactionRepositoryImpl: TransactionRepository{

    private companion object{
        val transactionRepo: MutableMap<String, ArrayDeque<Transaction>> = ConcurrentHashMap()
    }

    override fun saveTransaction(transaction: Transaction) = kotlin.runCatching {
        var accountTransactionsData = transactionRepo[transaction.originAccountId]
        if(accountTransactionsData == null) {
            accountTransactionsData = ArrayDeque()
            transactionRepo[transaction.originAccountId] = accountTransactionsData
        }
        accountTransactionsData.addLast(transaction.copy(id = UUID.randomUUID()))
        accountTransactionsData.last()
    }

}