package com.tnfigueiredo.tinybank.bdd.user

import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When


class UserStepsDefinition {

    @Given("a client with name {string}, surname {string}, document type {string}, document {string}, country {string}")
    fun a_new_client_with_name_surname_document_type_document_country(
        name: String?,
        surname: String?,
        doctype: String?,
        document: String?,
        country: String?
    ) {
        //TODO Implement
    }

    @Given("the client identification for document type {string}, document {string}, country {string}")
    fun the_client_identification_for_document_type_document_country(
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
        //TODO Implement
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