package com.tnfigueiredo.tinybank.bdd.steps

import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.model.DocType.NATIONAL_ID
import com.tnfigueiredo.tinybank.model.RestResponse
import com.tnfigueiredo.tinybank.model.Transaction
import com.tnfigueiredo.tinybank.model.TransactionType.DEPOSIT
import com.tnfigueiredo.tinybank.model.UserDTO
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import net.serenitybdd.core.Serenity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*


class TransactionStepsDefinition {

    private companion object{
        const val AGENCY = 123.toShort()
        const val BASE_SERVICE_PATH = "/transactions"
        const val USERS_BASE_SERVICE_PATH = "/users"
        const val ACCOUNT_BASE_SERVICE_PATH = "/accounts"
        lateinit var accountToBeUsed: Account
        lateinit var deactivatedAccountToBeUsed: Account
        lateinit var result: ResponseEntity<RestResponse>
        var A_RANDOM_USER_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
        var ANOTHER_RANDOM_USER_ID: UUID = UUID.fromString("0865ef31-9e22-45e7-a8fd-0d4a815f8fe7")
        var flowActiveUSer = true
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Given("the user has an active account in Tyny Bank")
    fun the_user_has_an_active_account_in_tyny_bank() {
        val aUserResult = restTemplate.postForEntity(USERS_BASE_SERVICE_PATH,
            UserDTO(name = "A_NAME", surname = "A_SURNAME", docType = NATIONAL_ID, document = "A_DOCUMENT", docCountry = "A_DOC_COUNTRY"),
            RestResponse::class.java
        )

        aUserResult.statusCode shouldBeEqual HttpStatus.OK
        A_RANDOM_USER_ID = (aUserResult.body?.data as UserDTO).id!!

        val activeAccountResult = restTemplate.postForEntity("${ACCOUNT_BASE_SERVICE_PATH}/user/${A_RANDOM_USER_ID}/agency/$AGENCY", null, RestResponse::class.java)

        activeAccountResult.statusCode shouldBeEqual HttpStatus.OK
        accountToBeUsed = activeAccountResult.body?.data as Account

        flowActiveUSer = true
    }

    @Given("the user has a deactivated account in Tyny Bank")
    fun the_user_has_a_deactivated_account_in_tyny_bank() {
        val anotherUserResult = restTemplate.postForEntity(USERS_BASE_SERVICE_PATH,
            UserDTO(name = "ANOTHER_NAME", surname = "ANOTHER_SURNAME", docType = NATIONAL_ID, document = "ANOTHER_DOCUMENT", docCountry = "ANOTHER_DOC_COUNTRY"),
            RestResponse::class.java
        )

        anotherUserResult.statusCode shouldBeEqual HttpStatus.OK

        val userDocType = (anotherUserResult.body?.data as UserDTO).docType
        val document = (anotherUserResult.body?.data as UserDTO).document
        val docCountry = (anotherUserResult.body?.data as UserDTO).docCountry

        restTemplate.postForEntity(
            "${ACCOUNT_BASE_SERVICE_PATH}/user/${A_RANDOM_USER_ID}/agency/$AGENCY",
            null,
            RestResponse::class.java
        ).statusCode shouldBe HttpStatus.OK

        restTemplate.delete(
            "${USERS_BASE_SERVICE_PATH}/docType/${userDocType}/document/${document}/country/${docCountry}",
            RestResponse::class.java
        )

        val deactivatedUserAndAccountResult = restTemplate.getForEntity(
            "${USERS_BASE_SERVICE_PATH}/docType/${userDocType}/document/${document}/country/${docCountry}",
            RestResponse::class.java
        )

        ANOTHER_RANDOM_USER_ID = (deactivatedUserAndAccountResult.body?.data as UserDTO).id!!
        deactivatedAccountToBeUsed = (deactivatedUserAndAccountResult.body?.data as UserDTO).account!!

        flowActiveUSer = false
    }

    @When("the user makes a deposit of {string}")
    fun the_user_makes_a_deposit_of(transactionValue: String) {
        result = if(flowActiveUSer) {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = accountToBeUsed.id!!,
                    amount = transactionValue.toDouble(),
                    type = DEPOSIT
                ),
                RestResponse::class.java
            )

        } else {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = deactivatedAccountToBeUsed.id!!,
                    amount = transactionValue.toDouble(),
                    type = DEPOSIT
                ),
                RestResponse::class.java
            )
        }

    }

    @Then("the account balance is increased by {string}")
    fun the_account_balance_is_increased_by(string: String?) {
        Serenity.recordReportData().withTitle("Client Account Initial State").andContents(accountToBeUsed.toString())
    }

    @Then("the transaction operation fails")
    fun the_transaction_operation_fails() {
        Serenity.recordReportData().withTitle("Client Account Initial State").andContents(deactivatedAccountToBeUsed.toString())
    }

}