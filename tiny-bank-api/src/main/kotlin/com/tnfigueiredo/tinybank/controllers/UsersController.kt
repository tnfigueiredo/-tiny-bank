package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.*
import com.tnfigueiredo.tinybank.services.AccountService
import com.tnfigueiredo.tinybank.services.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to the User entity.")
class UsersController() {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var accountService: AccountService

    @PostMapping
    fun createUser(@RequestBody user: UserDTO): ResponseEntity<RestResponse> =
        userService.createOrUpdateUser(User(name = user.name, surname = user.surname, docType = user.docType, document = user.document, docCountry = user.docCountry)).fold(
            onSuccess = { savedUser ->
                ResponseEntity.ok(
                    RestResponse(
                        message = "User Registered Successfully",
                        data = UserDTO(savedUser.id, savedUser.name, savedUser.surname, savedUser.docType, savedUser.document, savedUser.docCountry, savedUser.status)
                    )
                )
            },
            onFailure = { failure -> handleServiceCallFailure(failure)}
        )

    @GetMapping("/docType/{docType}/document/{document}/country/{docCountry}")
    fun getUserByDocument(
        @PathVariable docType: DocType,
        @PathVariable document: String,
        @PathVariable docCountry: String
    ): Any =
        userService.findUserByDocument(docType, document, docCountry).fold(
            onSuccess = { user ->
                if (user == null)
                    ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(RestResponse(message = "User Not Found for documents."))
                else
                    ResponseEntity.ok(RestResponse(data =
                        UserDTO(
                            id = user.id,
                            name = user.name,
                            surname =  user.surname,
                            docType =  user.docType,
                            document =  user.document,
                            docCountry =  user.docCountry,
                            status = user.status,
                            account = accountService.getAccountByUserId(user.id!!).getOrNull()
                        )
                    ))
            },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )

    @DeleteMapping("/docType/{docType}/document/{document}/country/{docCountry}")
    fun deactivateUserByDocument(
        @PathVariable docType: DocType,
        @PathVariable document: String,
        @PathVariable docCountry: String
    ): ResponseEntity<RestResponse> =
        userService.findUserByDocument(docType, document, docCountry).fold(
            onSuccess = { user ->
                if (user == null) {
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RestResponse(message = "User Not Found for documents."))
                } else {
                    var account: Account? = null
                    accountService.getAccountByUserId(user.id!!)
                        .onFailure { handleServiceCallFailure(it) }
                        .getOrNull()?.let { bankAccount ->
                            account = accountService.deactivateAccount(bankAccount.id!!)
                                .onFailure { handleServiceCallFailure(it) }.getOrNull()
                        }
                    userService.deactivateUser(user.id).fold(
                        onSuccess = { deactivatedUser ->
                            ResponseEntity.ok(
                                RestResponse(
                                    message = "User Deactivated Successfully",
                                    data = UserDTO(
                                        deactivatedUser.id,
                                        deactivatedUser.name,
                                        deactivatedUser.surname,
                                        deactivatedUser.docType,
                                        deactivatedUser.document,
                                        deactivatedUser.docCountry,
                                        deactivatedUser.status,
                                        account)
                                )
                            )
                        },
                        onFailure = { failure -> handleServiceCallFailure(failure)}
                    )
                }
            },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )

    @PutMapping("/docType/{docType}/document/{document}/country/{docCountry}")
    fun activateUserByDocument(
        @PathVariable docType: DocType,
        @PathVariable document: String,
        @PathVariable docCountry: String,
        @RequestBody userToActivate: UserDTO
    ): ResponseEntity<RestResponse> =
        userService.findUserByDocument(docType, document, docCountry).fold(
            onSuccess = { user ->
                if (user == null) {
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RestResponse(message = "User Not Found for documents."))
                } else {
                    userService.activateUser(user.copy(name = userToActivate.name, surname = userToActivate.surname)).fold(
                        onSuccess = { activatedUser ->
                            val bankAccount = accountService.getAccountByUserId(activatedUser.id!!)
                                .onFailure { handleServiceCallFailure(it) }
                                .getOrNull()
                            if (bankAccount != null) {
                                accountService.activateAccount(bankAccount.id!!)
                                    .onFailure { handleServiceCallFailure(it) }
                                    .getOrNull()!!.let {
                                        ResponseEntity.ok().body(RestResponse(
                                            data = UserDTO(
                                                activatedUser.id,
                                                activatedUser.name,
                                                activatedUser.surname,
                                                activatedUser.docType,
                                                activatedUser.document,
                                                activatedUser.docCountry,
                                                activatedUser.status,
                                                it
                                            )
                                        ))
                                    }
                            } else {
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestResponse(message = "Account Not Found for User."))
                            }
                        },
                        onFailure = { failure -> handleServiceCallFailure(failure) }
                    )
                }
            },
            onFailure = { failure -> handleServiceCallFailure(failure) }
        )

    private fun handleServiceCallFailure(e: Throwable) =
        when(e){
            is DataNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestResponse(message = e.message))
            is Exception -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestResponse(message = e.message))
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestResponse(message = e.message))
        }
}