package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.NoConsistentDataException
import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.model.ActivationStatus.ACTIVE
import java.util.concurrent.ConcurrentHashMap

interface AccountRepository {

    fun saveAccount(account: Account): Result<Account>

    fun deactivateAccount(id: String)

    fun findLatestAccount(agencyAccountPrefix: String):String?

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
                val accountToBeSaved = account.copy(balance = 0.0, status = ACTIVE)
                accountRepo[account.id] = accountToBeSaved
                accountToBeSaved
            }
        }
    }

    override fun deactivateAccount(id: String) {
        TODO("Not yet implemented")
    }

    override fun findLatestAccount(agencyAccountPrefix: String):String? = accountRepo.keys.filter { it.startsWith(agencyAccountPrefix) }.maxOrNull()
}