package com.tnfigueiredo.tinybank.exceptions

class DataNotFoundException(msg: String): Exception(msg)

class DataDuplicatedException(msg: String): Exception(msg)

class BusinessRuleValidationException(msg: String): Exception(msg)

class NoConsistentDataException(msg: String): Exception(msg)