package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.handleServiceCallFailure
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.services.AccountService
import com.tnfigueiredo.tinybank.services.TransactionService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Operations related to the account Transaction entity.")
class TransactionsController {

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    private lateinit var accountService: AccountService

    @PostMapping
    fun createTransaction(@RequestBody transaction: Transaction): ResponseEntity<RestResponse> =
        accountService.getAccountById(transaction.originAccountId).fold(
            onSuccess = { account ->
                if (account.isAccountActive()) {
                    accountService.updateAccountBalance(account.id!!, account.balance + transaction.amount)
                        .onFailure { failure -> handleServiceCallFailure(failure) }
                    transactionService.saveTransaction(transaction.copy(accountBalanceCurrentValue = account.balance))
                        .fold(
                            onSuccess = { savedTransaction ->
                                ResponseEntity.ok(
                                    RestResponse(
                                        message = "Transaction Registered Successfully",
                                        data = savedTransaction
                                    )
                                )
                            },
                            onFailure = { failure -> handleServiceCallFailure(failure) }
                        )
                } else {
                    ResponseEntity.badRequest().body(RestResponse(message = "Account is not active"))
                }
            },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )

    @GetMapping("/account/{accountId}/start/{startDate}")
    fun getTransactionByAccountIdAndDateRange(
        @PathVariable accountId: String,
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate? = null
    ): ResponseEntity<RestResponse> =
        transactionService.getTransactionsByDateRange(
            accountId,
            startDate.atStartOfDay(),
            if (endDate == null) startDate.atStartOfDay().plusDays(30) else endDate.atStartOfDay()
        ).fold(
            onSuccess = { transactions ->
                ResponseEntity.ok(
                    RestResponse(
                        message = "Transactions found",
                        data = transactions
                    )
                )
            },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )

}