package com.tnfigueiredo.tinybank.controllers

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.EntityError
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.services.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v1.0/users")
@Tag(name = "Users", description = "Operations related to the User entity.")
class UsersController() {

    @Autowired
    lateinit var userService: UserService

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<Any>  =
        userService.createOrUpdateUser(user).fold(
                onSuccess = { savedUser -> ResponseEntity.ok(savedUser) },
                onFailure = { failure ->
                    val error = EntityError(failure.message)
                    if (failure is DataNotFoundException) {
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
                    } else {
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
                    }
                }
            )

}