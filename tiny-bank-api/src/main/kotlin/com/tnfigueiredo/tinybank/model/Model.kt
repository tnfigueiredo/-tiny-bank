package com.tnfigueiredo.tinybank.model

import com.tnfigueiredo.tinybank.exceptions.NoConsistentDataException
import com.tnfigueiredo.tinybank.model.ActivationStatus.ACTIVE
import com.tnfigueiredo.tinybank.model.ActivationStatus.DEACTIVATED
import java.util.UUID

data class RestResponse(
    val message: String? = null,
    val data: Any? = null
)

data class User(
    val id: UUID? = null,
    val name: String,
    val surname: String,
    val docType: DocType,
    val document: String,
    val docCountry: String,
    val status: ActivationStatus = ACTIVE
) {
    fun isUserDocument(docType: DocType, document: String, docCountry: String): Boolean =
        this.docType == docType && this.document == document && this.docCountry == docCountry

    fun isUserActive(): Boolean = this.status == ACTIVE

    fun deactivateUser(): User = this.copy(status = DEACTIVATED)

    fun activateUser(): User = this.copy(status = ACTIVE)
}

data class Account(
    val id: String? = null,
    val agency: Short? = null,
    val year: Short? = null,
    val userId: UUID,
    val balance: Double = "0.0".toDouble(),
    val status: ActivationStatus = ACTIVE
){
    fun isAccountActive(): Boolean = this.status == ACTIVE

    fun deactivateAccount(): Account = this.copy(status = DEACTIVATED)
}

enum class DocType{
    NATIONAL_ID,
    PASSPORT
}

enum class ActivationStatus{
    ACTIVE, DEACTIVATED
}