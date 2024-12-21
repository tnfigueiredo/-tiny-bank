package com.tnfigueiredo.tinybank.bdd.steps

import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.model.DocType
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.User
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import net.serenitybdd.core.Serenity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*


class AccountManagementStepsDefinition {

    private companion object{
        const val A_USER_NAME = "NAME"
        const val A_USER_SURNAME = "SURNAME"
        const val BASE_SERVICE_PATH = "/accounts"
        const val USERS_BASE_SERVICE_PATH = "/users"
        lateinit var result: ResponseEntity<RestResponse>
        lateinit var invalidDocResponse: ResponseEntity<RestResponse>
        var accountUser: User? = null
        lateinit var userInfoToSearch: User
        lateinit var accountAgency: String
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Given("a client for account creation with document identification: document type {string}, document {string}, country {string}")
    fun a_client_for_account_creation_with_document_identification_document_type_document_country(
        docType: String,
        document: String,
        docCountry: String
    ) {
        userInfoToSearch = User(name = A_USER_NAME, surname = A_USER_SURNAME, docType = DocType.valueOf(docType), document = document, docCountry = docCountry)
    }

    @And("the document for the client requesting the account creation is invalid")
    fun the_document_for_the_client_requesting_the_account_creation_is_invalid() {
        invalidDocResponse = restTemplate.getForEntity(
            "$USERS_BASE_SERVICE_PATH/docType/${DocType.PASSPORT}/document/A_DOCUMENT/country/A_DOC_COUNTRY",
            RestResponse::class.java
        )
        accountUser = invalidDocResponse.body!!.data as User?
    }

    @And("the client requesting the account creation has its register active")
    fun the_client_requesting_the_account_creation_has_its_register_active() {
        restTemplate.postForEntity(
            USERS_BASE_SERVICE_PATH,
            User(
                name = "NAME",
                surname = "SURNAME",
                docType = userInfoToSearch.docType,
                document = userInfoToSearch.document,
                docCountry = userInfoToSearch.docCountry
            ),
            RestResponse::class.java)

        val restResponse =
            restTemplate.getForEntity(
                "$USERS_BASE_SERVICE_PATH/docType/${userInfoToSearch.docType}/document/${userInfoToSearch.document}/country/${userInfoToSearch.docCountry}",
                RestResponse::class.java
            ).body!!

        if (restResponse.data != null) {
            accountUser = User(
                id = UUID.fromString((restResponse.data as Map<*, *>)["id"] as String),
                name = (restResponse.data as Map<*, *>)["name"] as String,
                surname = (restResponse.data as Map<*, *>)["surname"] as String,
                docType = DocType.valueOf((restResponse.data as Map<*, *>)["docType"] as String),
                document = (restResponse.data as Map<*, *>)["document"] as String,
                docCountry = (restResponse.data as Map<*, *>)["docCountry"] as String
            )
        }
        accountUser?.isUserActive()?.shouldBeTrue()
    }

    @And("the backoffice operation is from the agency {string}")
    fun the_backoffice_operation_is_from_the_agency(agency: String) {
        accountAgency = agency
    }

    @When("the account creation is requested")
    fun the_account_creation_is_requested() {
        result = restTemplate.postForEntity("$BASE_SERVICE_PATH/user/${accountUser?.id}/agency/$accountAgency", null, RestResponse::class.java)
    }

    @When("there is no user with the document information")
    fun there_is_no_user_with_document_information() {
        Serenity.recordReportData().withTitle("User Account Creations Document in Request").andContents("$USERS_BASE_SERVICE_PATH/docType/${DocType.PASSPORT}/document/A_DOCUMENT/country/A_DOC_COUNTRY")
    }

    @Then("the client's account is created successfully")
    fun the_client_s_account_is_created_successfully() {
        result.statusCode shouldBeEqual HttpStatus.OK
        Serenity.recordReportData().withTitle("User Account Creations Response").andContents(result.toString())
    }

    @Then("the client's account is not created")
    fun the_client_s_account_is_not_created() {
        invalidDocResponse.statusCode shouldBeEqual HttpStatus.NOT_FOUND
        invalidDocResponse.body!!.data.shouldBeNull()
        Serenity.recordReportData().withTitle("User Bank Account Creations Response").andContents(invalidDocResponse.toString())
    }

}