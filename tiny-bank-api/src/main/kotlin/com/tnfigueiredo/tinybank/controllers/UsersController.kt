package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.User
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

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<RestResponse> =
        userService.createOrUpdateUser(user).fold(
            onSuccess = { savedUser ->
                ResponseEntity.ok(
                    RestResponse(
                        message = "User Registered Successfully",
                        data = savedUser
                    )
                )
            },
            onFailure = { failure ->
                val error = RestResponse(failure.message)
                if (failure is DataNotFoundException) {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
                } else {
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                }
            }
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
                    ResponseEntity.ok(RestResponse(data = user))
            },
            onFailure = { failure ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestResponse(message = failure.message))
            }
        )

}