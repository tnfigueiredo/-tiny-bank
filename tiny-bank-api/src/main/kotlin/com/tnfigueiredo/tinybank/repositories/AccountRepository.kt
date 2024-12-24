package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.exceptions.NoConsistentDataException
import com.tnfigueiredo.tinybank.model.Account
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

interface AccountRepository {

    fun saveAccount(account: Account): Result<Account>

    fun getAccountById(id: String): Result<Account>

    fun getAccountByUserId(userId: UUID): Result<Account?>

    fun deactivateAccount(id: String): Result<Account>

    fun findLatestAccount(agencyAccountPrefix: String):String?

    fun deleteAll()

}

class AccountRepositoryImpl : AccountRepository {

    private companion object{
        val accountRepo: MutableMap<String, Account> = ConcurrentHashMap()
    }

    override fun saveAccount(account: Account): Result<Account> = kotlin.runCatching {
        when {
            account.id == null -> throw NoConsistentDataException("No id account informed.")
            accountRepo.containsKey(account.id) -> throw DataDuplicatedException("The account ${account.id} is duplicated")
            else -> {
                accountRepo[account.id] = account
                account
            }
        }
    }

    override fun getAccountById(id: String): Result<Account> = kotlin.runCatching {
        accountRepo[id] ?: throw DataNotFoundException("The account $id does not exist.")
    }

    override fun getAccountByUserId(userId: UUID): Result<Account?> = kotlin.runCatching {
        accountRepo.values.firstOrNull() { it.userId == userId }
    }

    override fun deactivateAccount(id: String) = kotlin.runCatching {
        accountRepo[id]?.let { accountRecovered ->
            accountRepo[id] = accountRecovered.deactivateAccount()
            accountRepo[id]
        } ?: throw DataNotFoundException("The account $id does not exist.")
    }

    override fun findLatestAccount(agencyAccountPrefix: String):String? = accountRepo.keys.filter { it.startsWith(agencyAccountPrefix) }.maxOrNull()

    override fun deleteAll() = accountRepo.clear()
}