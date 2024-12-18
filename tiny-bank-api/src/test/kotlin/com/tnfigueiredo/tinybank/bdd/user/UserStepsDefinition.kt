package com.tnfigueiredo.tinybank.bdd.user

import io.cucumber.java.PendingException
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When


class UserStepsDefinition {

    @Given("a new client with name {string}, surname {string}, document type {string}, document {string}, country {string}")
    fun a_new_client_with_name_surname_document_type_document_country(
        name: String?,
        surname: String?,
        doctype: String?,
        document: String?,
        country: String?
    ) {
        //TODO Implement
    }

    @And("there is no client with this document type and document identification")
    fun there_is_no_client_with_this_document_type_and_document_identification() {
        //TODO Implement
    }

    @Given("there is a client with the document type {string}, document {string}, country {string}")
    fun there_is_a_client_with_the_document_type_document_country(string: String?, string2: String?, string3: String?) {
        //TODO Implement
    }

    @When("the account creation is requested")
    fun the_account_creation_is_requested() {
        //TODO Implement
    }

    @Then("the client's register and account are created successfully")
    fun the_client_s_register_and_account_are_created_successfully() {
        //TODO Implement
    }

    @Then("the client's register and account are denied")
    fun the_client_s_register_and_account_are_denied() {
        //TODO Implement
    }

}