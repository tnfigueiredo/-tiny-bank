package com.tnfigueiredo.tinybank.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1.0/users")
@Tag(name = "Users", description = "Operations related to the User entity.")
class UsersResource {

    @Operation(summary = "Say Hello", description = "Returns a greeting message.")
    @GetMapping("/hello")
    fun sayHello(): String {
        return "Hello, OpenAPI with Kotlin!"
    }

}