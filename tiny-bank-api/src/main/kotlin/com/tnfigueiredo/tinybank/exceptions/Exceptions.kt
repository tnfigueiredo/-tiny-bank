package com.tnfigueiredo.tinybank.exceptions

import com.tnfigueiredo.tinybank.model.RestResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class DataNotFoundException(msg: String): Exception(msg)

class DataDuplicatedException(msg: String): Exception(msg)

class BusinessRuleValidationException(msg: String): Exception(msg)

class NoConsistentDataException(msg: String): Exception(msg)

class TransactionDeniedException(msg: String): Exception(msg)

fun handleServiceCallFailure(e: Throwable) =
    when(e){
        is DataNotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestResponse(message = e.message))
        is Exception -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestResponse(message = e.message))
        else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestResponse(message = e.message))
    }