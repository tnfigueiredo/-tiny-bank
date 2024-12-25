package com.tnfigueiredo.tinybank.bdd.steps

import com.tnfigueiredo.tinybank.model.*
import com.tnfigueiredo.tinybank.model.ActivationStatus.ACTIVE
import com.tnfigueiredo.tinybank.model.ActivationStatus.DEACTIVATED
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import net.serenitybdd.core.Serenity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*


class UserStepsDefinition {

    private companion object{
        lateinit var userToSubmit: UserDTO
        var userToBeDeactivated: UserDTO? = null
        lateinit var result: ResponseEntity<RestResponse>
        const val BASE_SERVICE_PATH = "/users"
        const val ACCOUNT_BASE_SERVICE_PATH = "/accounts"
        var deactivatingExistingUser = true
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Given("a client with name {string}, surname {string}, document type {string}, document {string}, country {string}")
    fun a_new_client_with_name_surname_document_type_document_country(
        name: String,
        surname: String,
        doctype: String,
        document: String,
        country: String
    ) {
        userToSubmit = UserDTO(null, name, surname, DocType.valueOf(doctype), document, country)
    }

    @Given("the client identification for document type {string}, document {string}, country {string}")
    fun the_client_identification_for_document_type_document_country(
        doctype: String,
        document: String,
        country: String
    ) {
        userToSubmit = UserDTO(null, "ANOTHER_NAME", "ANOTHER_SURNAME", DocType.valueOf(doctype), document, country)
        restTemplate.postForEntity(BASE_SERVICE_PATH, userToSubmit, RestResponse::class.java)
    }

    @And("there is a client with the document type {string}, document {string}, country {string}")
    fun there_is_a_client_with_the_document_type_document_country(doctype: String, document: String, country: String) {
        val existingUser = User(name = "ANOTHER_NAME", surname = "ANOTHER_SURNAME", docType = DocType.valueOf(doctype), document = document, docCountry = country)
        val existingUserResult = restTemplate.postForEntity(BASE_SERVICE_PATH, existingUser, RestResponse::class.java)
        existingUserResult.statusCode shouldBeEqual HttpStatus.OK
        existingUserResult.body?.data.shouldNotBeNull()
    }

    @And("there is a client with this document information")
    fun there_is_a_client_with_this_document_information() {
        val result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        result.statusCode shouldBeEqual HttpStatus.OK
        result.body?.data.shouldNotBeNull()

        userToBeDeactivated = UserDTO(
            id = UUID.fromString((result.body!!.data as Map<*, *>)["id"] as String),
            name = (result.body!!.data as Map<*, *>)["name"] as String,
            surname = (result.body!!.data as Map<*, *>)["surname"] as String,
            docType = DocType.valueOf((result.body!!.data as Map<*, *>)["docType"] as String),
            document = (result.body!!.data as Map<*, *>)["document"] as String,
            docCountry = (result.body!!.data as Map<*, *>)["docCountry"] as String,
            status = ActivationStatus.valueOf((result.body!!.data as Map<*, *>)["status"] as String)
        )
    }

    @And("there is no client account")
    fun there_is_no_client_account() {
        userToBeDeactivated?.account.shouldBeNull()
    }

    @And("there is a client account")
    fun there_is_a_client_account() {
        val accountResult = restTemplate.postForEntity("${ACCOUNT_BASE_SERVICE_PATH}/user/${userToBeDeactivated?.id}/agency/0001", null, RestResponse::class.java)
        accountResult.statusCode shouldBeEqual HttpStatus.OK

        val result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        result.statusCode shouldBeEqual HttpStatus.OK
        result.body?.data.shouldNotBeNull()

        val accountData = (result.body!!.data as Map<*, *>)["account"] as Map<*, *>
        userToBeDeactivated = UserDTO(
            id = UUID.fromString((result.body!!.data as Map<*, *>)["id"] as String),
            name = (result.body!!.data as Map<*, *>)["name"] as String,
            surname = (result.body!!.data as Map<*, *>)["surname"] as String,
            docType = DocType.valueOf((result.body!!.data as Map<*, *>)["docType"] as String),
            document = (result.body!!.data as Map<*, *>)["document"] as String,
            docCountry = (result.body!!.data as Map<*, *>)["docCountry"] as String,
            status = ActivationStatus.valueOf((result.body!!.data as Map<*, *>)["status"] as String),
            account = Account(
                id = accountData["id"] as String,
                agency = accountData["agency"].toString().toShort(),
                year = accountData["year"].toString().toShort(),
                userId = UUID.fromString(accountData["userId"] as String),
                balance = accountData["balance"].toString().toDouble(),
                status = ActivationStatus.valueOf(accountData["status"] as String)
            )
        )

        userToBeDeactivated?.account.shouldNotBeNull()
        Serenity.recordReportData().withTitle("User Client Account").andContents(userToBeDeactivated?.account.toString())
    }

    @And("there is no client with this document information")
    fun there_is_no_client_with_this_document_information() {
        deactivatingExistingUser = false
    }


    @When("the register creation is requested")
    fun the_register_creation_is_requested() {
        result = restTemplate.postForEntity(BASE_SERVICE_PATH, userToSubmit, RestResponse::class.java)
    }

    @When("the account activation is requested")
    fun the_account_activation_is_requested() {
        val result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        result.statusCode shouldBeEqual HttpStatus.OK
        result.body?.data.shouldNotBeNull()

        restTemplate.delete(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        val delResult = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        delResult.statusCode shouldBeEqual HttpStatus.OK
        delResult.body?.data.shouldNotBeNull()

        restTemplate.put(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            UserDTO(
                id = null,
                name = userToSubmit.name,
                surname = userToSubmit.surname,
                docType = userToSubmit.docType,
                document = userToSubmit.document,
                docCountry = userToSubmit.docCountry,
                status = userToSubmit.status
            )
        )
    }

    @When("the user deactivation is requested")
    fun the_user_deactivation_is_requested() {
        if (deactivatingExistingUser) {
            restTemplate.delete(
                "$BASE_SERVICE_PATH/docType/${userToBeDeactivated!!.docType}/document/${userToBeDeactivated!!.document}/country/${userToBeDeactivated!!.docCountry}",
                RestResponse::class.java
            )
            result = restTemplate.getForEntity(
                "$BASE_SERVICE_PATH/docType/${userToBeDeactivated!!.docType}/document/${userToBeDeactivated!!.document}/country/${userToBeDeactivated!!.docCountry}",
                RestResponse::class.java
            )
        } else{
            result = restTemplate.exchange(
                "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                RestResponse::class.java
            )
        }
    }

    @Then("the client's register is created successfully")
    fun the_client_s_register_is_created_successfully() {
        result.statusCode shouldBeEqual HttpStatus.OK
        Serenity.recordReportData().withTitle("User Registration Response").andContents(result.toString())
    }

    @Then("the client's register is denied due to duplicated documentation information")
    fun the_client_s_register_and_account_are_denied() {
        result.statusCode shouldBeEqual HttpStatus.BAD_REQUEST
        Serenity.recordReportData().withTitle("User Registration Response").andContents(result.toString())
    }

    @Then("the client's register and account are reactivated")
    fun the_client_s_account_is_deactivated() {
        result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/docType/${userToSubmit.docType}/document/${userToSubmit.document}/country/${userToSubmit.docCountry}",
            RestResponse::class.java
        )

        result.statusCode shouldBeEqual HttpStatus.OK
        result.body?.data.shouldNotBeNull()
        ActivationStatus.valueOf((result.body!!.data as Map<*, *>)["status"] as String) shouldBeEqual ACTIVE
        if((result.body!!.data as Map<*, *>).containsKey("account") && (result.body!!.data as Map<*, *>)["account"] != null){
            ActivationStatus.valueOf(((result.body!!.data as Map<*, *>)["account"] as Map<*, *>)["status"] as String) shouldBeEqual ACTIVE
        }
    }

    @Then("the user data is updated")
    fun the_user_data_is_updated() {
        (result.body!!.data as Map<*, *>)["name"]?.shouldBeEqual(userToSubmit.name)
        (result.body!!.data as Map<*, *>)["surname"]?.shouldBeEqual(userToSubmit.surname)
        (result.body!!.data as Map<*, *>)["docType"]?.shouldBeEqual(userToSubmit.docType.name)
        (result.body!!.data as Map<*, *>)["document"]?.shouldBeEqual(userToSubmit.document)
        (result.body!!.data as Map<*, *>)["docCountry"]?.shouldBeEqual(userToSubmit.docCountry)
        (result.body!!.data as Map<*, *>)["status"]?.shouldBeEqual(userToSubmit.status.name)
    }

    @Then("the client's account deactivation is denied")
    fun the_client_s_account_deactivation_is_denied() {
        result.statusCode shouldBeEqual HttpStatus.NOT_FOUND
        Serenity.recordReportData().withTitle("User Deactivation Response").andContents(result.toString())
    }

    @And("the client's account is deactivated")
    fun the_client_s_account_data_is_deactivated() {
        result.statusCode shouldBeEqual HttpStatus.OK
        ActivationStatus.valueOf((result.body!!.data as Map<*, *>)["status"] as String) shouldBeEqual DEACTIVATED
        if((result.body!!.data as Map<*, *>).containsKey("account") && (result.body!!.data as Map<*, *>)["account"] != null){
            ActivationStatus.valueOf(((result.body!!.data as Map<*, *>)["account"] as Map<*, *>)["status"] as String) shouldBeEqual DEACTIVATED
        }
        Serenity.recordReportData().withTitle("User Deactivation Response").andContents(result.toString())
    }

    @Then("the client's data matches the submitted date")
    fun the_client_s_data_matches_the_submitted_date() {
        result.body.shouldNotBeNull()
        (result.body!!.data as Map<*, *>)["name"]?.shouldBeEqual(userToSubmit.name)
        (result.body!!.data as Map<*, *>)["surname"]?.shouldBeEqual(userToSubmit.surname)
        (result.body!!.data as Map<*, *>)["docType"]?.shouldBeEqual(userToSubmit.docType.name)
        (result.body!!.data as Map<*, *>)["document"]?.shouldBeEqual(userToSubmit.document)
        (result.body!!.data as Map<*, *>)["docCountry"]?.shouldBeEqual(userToSubmit.docCountry)
        (result.body!!.data as Map<*, *>)["status"]?.shouldBeEqual(userToSubmit.status.name)

        Serenity.recordReportData().withTitle("User Registration Response")
            .andContents(
                """
                    Submitted data: $userToSubmit
                    API Response data: ${result.body!!.data}
                """.trimIndent()
            )
    }

}