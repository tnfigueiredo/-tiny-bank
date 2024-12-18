package com.tnfigueiredo.tinybank.model

import java.util.UUID

data class User(
    val id: UUID? = null,
    val name: String,
    val surname: String,
    val docType: DocType,
    val document: String,
    val docCountry: String
)

enum class DocType{
    NATIONAL_ID,
    PASSPORT
}