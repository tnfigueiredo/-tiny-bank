package com.tnfigueiredo.tinybank.bdd.user

import com.tnfigueiredo.tinybank.model.DocType
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
import java.util.*


class UserStepsDefinition {

    private companion object{
        lateinit var userToSubmit: User
        lateinit var result: ResponseEntity<User>
        const val BASE_SERVICE_PATH = "/v1.0/users"
        val A_RANDOM_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
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

    @Given("there is a client with the document type {string}, document {string}, country {string}")
    fun there_is_a_client_with_the_document_type_document_country(string: String?, string2: String?, string3: String?) {
        //TODO Implement
    }

    @When("the account creation is requested")
    fun the_account_creation_is_requested() {
        result = restTemplate.postForEntity(BASE_SERVICE_PATH, userToSubmit, User::class.java)
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

    @Then("the client's register and account are created successfully")
    fun the_client_s_register_and_account_are_created_successfully() {
        result.shouldNotBeNull()
        result.statusCode shouldBeEqual HttpStatus.OK
        result.body.shouldNotBeNull()
    }

    @Then("the client's register and account are denied")
    fun the_client_s_register_and_account_are_denied() {
        //TODO Implement
    }

    @Then("the client's account is activated")
    fun the_client_s_account_is_activated() {
        //TODO Implement
    }

    @Then("the client's account is deactivated")
    fun the_client_s_account_is_deactivated() {
        //TODO Implement
    }

    @Then("the client's account deactivation is denied")
    fun the_client_s_account_deactivation_is_denied() {
        //TODO Implement
    }

    @And("the client's account data is updated")
    fun the_client_s_account_data_is_updated() {
        //TODO Implement
    }

}