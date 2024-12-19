package com.tnfigueiredo.tinybank.services

import com.sun.jdi.ShortType
import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.repositories.AccountRepository
import java.time.LocalDate
import java.util.UUID

interface AccountService{

    fun createAccount(userId: UUID, agency: Short, year: Short): Result<Account>

}

class AccountServiceImpl(private val accountRepository: AccountRepository):AccountService{

    override fun createAccount(userId: UUID, agency: Short, year:Short): Result<Account> = kotlin.runCatching{
        val accountToBeSaved = Account(
            id = getNextAgencyAccountIdentification("$year${String.format("%04d", agency)}"),
            userId = userId,
            agency = agency,
            year = year
        )
        val result = accountRepository.saveAccount(accountToBeSaved)
        result.onFailure { throw it }
        result.getOrNull()!!
    }

    private fun getNextAgencyAccountIdentification(agencyAccountPrefix: String): String {
        val latestAccount = accountRepository.findLatestAccount(agencyAccountPrefix)
        return if (latestAccount.isNullOrBlank()) "$agencyAccountPrefix${String.format("%04d", 0)}" else (latestAccount.toLong() + 1).toString()
    }
}