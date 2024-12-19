package com.tnfigueiredo.tinybank.model

import java.util.UUID

data class EntityError(
    val message: String?
)

data class User(
    val id: UUID? = null,
    val name: String,
    val surname: String,
    val docType: DocType,
    val document: String,
    val docCountry: String
) {
    fun isUserDocument(docType: DocType, document: String, docCountry: String): Boolean =
        this.docType == docType && this.document == document && this.docCountry == docCountry
}

enum class DocType{
    NATIONAL_ID,
    PASSPORT
}