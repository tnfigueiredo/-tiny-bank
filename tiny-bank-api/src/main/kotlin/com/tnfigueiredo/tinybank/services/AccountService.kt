package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.repositories.AccountRepository
import java.util.*

interface AccountService{

    fun createAccount(userId: UUID, agency: Short, year: Short): Result<Account>

    fun getAccountByUserId(userId: UUID): Result<Account?>

    fun getAccountById(accountId: String): Result<Account>

    fun deactivateAccount(accountId: String): Result<Account>

    fun activateAccount(accountId: String): Result<Account>

    fun updateAccountBalance(accountId: String, amount: Double): Result<Account>

}

class AccountServiceImpl(private val accountRepository: AccountRepository):AccountService{

    override fun createAccount(userId: UUID, agency: Short, year:Short): Result<Account> = kotlin.runCatching{
        val accountToBeSaved = Account(
            id = getNextAgencyAccountIdentification("$year${String.format("%04d", agency)}"),
            userId = userId,
            agency = agency,
            year = year
        )

        if(accountRepository.getAccountByUserId(userId).getOrNull() != null)
            throw BusinessRuleValidationException("The user already has an account.")

        accountRepository.saveAccount(accountToBeSaved)
            .onFailure { throw it }
            .getOrNull()!!
    }

    override fun getAccountByUserId(userId: UUID): Result<Account?> = kotlin.runCatching {
        accountRepository.getAccountByUserId(userId).getOrNull()
    }

    override fun getAccountById(accountId: String): Result<Account> = kotlin.runCatching {
        accountRepository.getAccountById(accountId)
            .onFailure { throw it }
            .getOrNull()!!
    }

    override fun deactivateAccount(accountId: String): Result<Account> = kotlin.runCatching{
        val account = accountRepository.getAccountById(accountId)
            .onFailure { throw it }
            .getOrNull()!!

        if(account.isAccountActive().not())
            throw BusinessRuleValidationException("The account $accountId is already deactivated.")

        if(account.balance > 0.0)
            throw BusinessRuleValidationException("The account $accountId has a balance greater than zero.")

        accountRepository.deactivateAccount(accountId)
            .onFailure { throw it }
            .getOrNull()!!
    }

    override fun activateAccount(accountId: String): Result<Account> = kotlin.runCatching{
        val account = accountRepository.getAccountById(accountId)
            .onFailure { throw it }
            .getOrNull()!!

        if(account.isAccountActive())
            throw BusinessRuleValidationException("The account $accountId is already activated.")

        accountRepository.activateAccount(accountId)
            .onFailure { throw it }
            .getOrNull()!!
    }

    override fun updateAccountBalance(accountId: String, amount: Double): Result<Account> = kotlin.runCatching {
        if (amount < 0.0)
            throw BusinessRuleValidationException("The account balance must be greater than zero.")
        accountRepository.updateAccountBalance(accountId, amount)
            .onFailure { throw it }
            .getOrNull()!!
    }

    private fun getNextAgencyAccountIdentification(agencyAccountPrefix: String): String {
        val latestAccount = accountRepository.findLatestAccount(agencyAccountPrefix)
        return if (latestAccount.isNullOrBlank()) "$agencyAccountPrefix${String.format("%04d", 0)}" else (latestAccount.toLong() + 1).toString()
    }
}