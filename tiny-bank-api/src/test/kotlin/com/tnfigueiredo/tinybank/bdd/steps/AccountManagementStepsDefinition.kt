package com.tnfigueiredo.tinybank.bdd.steps

import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When


class AccountManagementStepsDefinition {

    @Given("a client for account creation with document identification: document type {string}, document {string}, country {string}")
    fun a_client_for_account_creation_with_document_identification_document_type_document_country(
        string: String?,
        string2: String?,
        string3: String?
    ) {
        //TODO Implement
    }

    @Given("the document for the client requesting the account creation is invalid")
    fun the_document_for_the_client_requesting_the_account_creation_is_invalid() {
        //TODO Implement
    }

    @And("the client requesting the account creation has its register active")
    fun the_client_requesting_the_account_creation_has_its_register_active() {
        //TODO Implement
    }

    @When("the account creation is requested")
    fun the_account_creation_is_requested() {
        //TODO Implement
    }

    @Then("the client's account is created successfully")
    fun the_client_s_account_is_created_successfully() {
        //TODO Implement
    }

    @Then("the client's account creation is denied")
    fun the_client_s_account_creation_is_denied() {
        //TODO Implement
    }

}