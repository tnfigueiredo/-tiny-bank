package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.handleServiceCallFailure
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.services.AccountService
import com.tnfigueiredo.tinybank.services.TransactionService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Operations related to the account Transaction entity.")
class TransactionsController {

    @Autowired
    lateinit var transactionService: TransactionService

    @Autowired
    lateinit var accountService: AccountService

    @PostMapping
    fun createTransaction(@RequestBody transaction: Transaction): ResponseEntity<RestResponse> =
        accountService.getAccountById(transaction.originAccountId).fold(
            onSuccess = { account ->
                if(account.isAccountActive()) {
                    accountService.updateAccountBalance(account.id!!, account.balance + transaction.amount)
                        .onFailure { failure -> handleServiceCallFailure(failure) }
                    transactionService.saveTransaction(transaction.copy(accountBalanceCurrentValue = account.balance)).fold(
                        onSuccess = { savedTransaction ->
                            ResponseEntity.ok(
                                RestResponse(
                                    message = "Transaction Registered Successfully",
                                    data = savedTransaction
                                )
                            )
                        },
                        onFailure = { failure -> handleServiceCallFailure(failure)}
                    )
                } else {
                    ResponseEntity.badRequest().body(RestResponse(message = "Account is not active"))
                }
            },
            onFailure = { failure -> handleServiceCallFailure(failure)}
        )

}