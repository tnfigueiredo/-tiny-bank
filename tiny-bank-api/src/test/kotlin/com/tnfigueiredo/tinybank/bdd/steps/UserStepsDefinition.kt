package com.tnfigueiredo.tinybank.bdd.steps

import com.tnfigueiredo.tinybank.model.DocType
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.User
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class UserStepsDefinition {

    private companion object{
        lateinit var userToSubmit: User
        lateinit var result: ResponseEntity<RestResponse>
        const val BASE_SERVICE_PATH = "/users"
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
        userToSubmit = User(null, name, surname, DocType.valueOf(doctype), document, country)
    }

    @Given("the client identification for document type {string}, document {string}, country {string}")
    fun the_client_identification_for_document_type_document_country(
        doctype: String,
        document: String,
        country: String
    ) {
        //TODO Implement
    }

    @And("there is a client with the document type {string}, document {string}, country {string}")
    fun there_is_a_client_with_the_document_type_document_country(doctype: String, document: String, country: String) {
        val existingUser = User(name = "ANOTHER_NAME", surname = "ANOTHER_SURNAME", docType = DocType.valueOf(doctype), document = document, docCountry = country)
        val existingUserResult = restTemplate.postForEntity(BASE_SERVICE_PATH, existingUser, RestResponse::class.java)
        existingUserResult.statusCode shouldBeEqual HttpStatus.OK
        existingUserResult.body?.data.shouldNotBeNull()
    }

    @When("the register creation is requested")
    fun the_account_creation_is_requested() {
        result = restTemplate.postForEntity(BASE_SERVICE_PATH, userToSubmit, RestResponse::class.java)
    }

    @When("the account activation is requested")
    fun the_account_activation_is_requested() {
        //TODO Implement
    }

    @When("the account deactivation is requested")
    fun the_account_deactivation_is_requested() {
        //TODO Implement
    }

    @When("the client identification have no record in Tiny Bank")
    fun the_client_identification_have_no_record_in_tiny_bank() {
        //TODO Implement
    }

    @Then("the client's register is created successfully")
    fun the_client_s_register_and_account_are_created_successfully() {
        result.statusCode shouldBeEqual HttpStatus.OK

    }

    @Then("the client's register is denied due to duplicated documentation information")
    fun the_client_s_register_and_account_are_denied() {
        result.statusCode shouldBeEqual HttpStatus.BAD_REQUEST
        result.body?.message?.shouldBeEqual("Duplicated user document: $userToSubmit.")
    }

    @Then("the client's account is activated")
    fun the_client_s_account_is_activated() {
        //TODO Implement
    }

    @Then("the client's register and account are reactivated")
    fun the_client_s_account_is_deactivated() {
        //TODO Implement
    }

    @Then("the client's account deactivation is denied")
    fun the_client_s_account_deactivation_is_denied() {
        //TODO Implement
    }

    @And("the client's account is deactivated")
    fun the_client_s_account_data_is_updated() {
        //TODO Implement
    }

    @Then("the user data is updated")
    fun the_user_data_is_updated() {
        //TODO Implement
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
    }

}