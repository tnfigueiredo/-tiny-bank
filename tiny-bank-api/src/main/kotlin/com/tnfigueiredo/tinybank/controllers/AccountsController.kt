package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.handleServiceCallFailure
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.services.AccountService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Operations related to the Account entity.")
@Validated
class AccountsController {

    @Autowired
    lateinit var accountService: AccountService

    @PostMapping("/user/{userId}/agency/{agency}")
    fun createAccount(@PathVariable  @NotBlank userId: String, @PathVariable @NotBlank agency:String): ResponseEntity<RestResponse> =
        accountService.createAccount(
            userId = UUID.fromString(userId),
            agency = agency.toShort(),
            year = LocalDate.now().year.toShort()
        ).fold(
            onSuccess = { createdAccount -> ResponseEntity.ok(RestResponse(message = "User Account created successfully", data = createdAccount) ) },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )
}