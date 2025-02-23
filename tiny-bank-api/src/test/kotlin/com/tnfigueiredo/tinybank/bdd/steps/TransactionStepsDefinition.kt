package com.tnfigueiredo.tinybank.bdd.steps

import com.tnfigueiredo.tinybank.model.*
import com.tnfigueiredo.tinybank.model.DocType.NATIONAL_ID
import com.tnfigueiredo.tinybank.model.TransactionType.*
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import net.serenitybdd.core.Serenity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.util.*


class TransactionStepsDefinition {

    private companion object {
        const val AGENCY = 123.toShort()
        const val BASE_SERVICE_PATH = "/transactions"
        const val USERS_BASE_SERVICE_PATH = "/users"
        const val ACCOUNT_BASE_SERVICE_PATH = "/accounts"
        var accountToBeUsed: Account? = null
        var deactivatedAccountToBeUsed: Account? = null
        var destinationTransferAccountToBeUsed: Account? = null
        lateinit var result: ResponseEntity<RestResponse>
        var A_RANDOM_USER_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
        var ANOTHER_RANDOM_USER_ID: UUID = UUID.fromString("0865ef31-9e22-45e7-a8fd-0d4a815f8fe7")
        var THIRD_RANDOM_USER_ID: UUID = UUID.fromString("0865ef31-9e22-45e7-a8fd-0d4a815f8fe7")
        var flowActiveUSer = true
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Given("the user has an active account in Tyny Bank")
    fun the_user_has_an_active_account_in_tyny_bank() {

        if (accountToBeUsed == null) {
            val aUserResult = restTemplate.postForEntity(
                USERS_BASE_SERVICE_PATH,
                UserDTO(
                    name = "A_NAME",
                    surname = "A_SURNAME",
                    docType = NATIONAL_ID,
                    document = "A_DOCUMENT_FOR_DEPOSIT",
                    docCountry = "A_DOC_COUNTRY"
                ),
                RestResponse::class.java
            )

            aUserResult.statusCode shouldBeEqual HttpStatus.OK
            A_RANDOM_USER_ID = UUID.fromString(((aUserResult.body!!.data as Map<*, *>)["id"] as String))

            val activeAccountResult = restTemplate.postForEntity(
                "${ACCOUNT_BASE_SERVICE_PATH}/user/${A_RANDOM_USER_ID}/agency/$AGENCY",
                null,
                RestResponse::class.java
            )

            activeAccountResult.statusCode shouldBeEqual HttpStatus.OK
            val account = (activeAccountResult.body!!.data as Map<*, *>)
            accountToBeUsed = Account(
                id = account["id"] as String,
                agency = account["agency"].toString().toShort(),
                year = account["year"].toString().toShort(),
                userId = UUID.fromString(account["userId"] as String),
                balance = account["balance"] as Double,
                status = ActivationStatus.valueOf(account["status"] as String)
            )
        }

        flowActiveUSer = true
    }

    @Given("the user has a deactivated account in Tyny Bank")
    fun the_user_has_a_deactivated_account_in_tyny_bank() {

        if (deactivatedAccountToBeUsed == null) {
            val anotherUserResult = restTemplate.postForEntity(
                USERS_BASE_SERVICE_PATH,
                UserDTO(
                    name = "ANOTHER_NAME",
                    surname = "ANOTHER_SURNAME",
                    docType = NATIONAL_ID,
                    document = "ANOTHER_DOCUMENT",
                    docCountry = "ANOTHER_DOC_COUNTRY"
                ),
                RestResponse::class.java
            )

            anotherUserResult.statusCode shouldBeEqual HttpStatus.OK

            ANOTHER_RANDOM_USER_ID = UUID.fromString(((anotherUserResult.body!!.data as Map<*, *>)["id"] as String))
            val userDocType = DocType.valueOf(((anotherUserResult.body!!.data as Map<*, *>)["docType"] as String))
            val document = ((anotherUserResult.body!!.data as Map<*, *>)["document"] as String)
            val docCountry = ((anotherUserResult.body!!.data as Map<*, *>)["docCountry"] as String)

            restTemplate.postForEntity(
                "${ACCOUNT_BASE_SERVICE_PATH}/user/${ANOTHER_RANDOM_USER_ID}/agency/$AGENCY",
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

            val account = ((deactivatedUserAndAccountResult.body!!.data as Map<*, *>)["account"] as Map<*, *>)
            deactivatedAccountToBeUsed = Account(
                id = account["id"] as String,
                agency = account["agency"].toString().toShort(),
                year = account["year"].toString().toShort(),
                userId = UUID.fromString(account["userId"] as String),
                balance = account["balance"] as Double,
                status = ActivationStatus.valueOf(account["status"] as String)
            )
        }

        flowActiveUSer = false
    }

    @And("the account balance is {string}")
    fun the_account_balance_is(depositValue: String) {
        restTemplate.postForEntity(
            BASE_SERVICE_PATH,
            Transaction(
                originAccountId = accountToBeUsed?.id!!,
                amount = depositValue.toDouble(),
                type = DEPOSIT
            ),
            RestResponse::class.java
        )

        accountToBeUsed = accountToBeUsed!!.copy(balance = accountToBeUsed!!.balance + depositValue.toDouble())
    }

    @And("the destination account {string} is chosen for transfer")
    fun the_destination_account_is_chosen_for_transfer(string: String?) {

        if (destinationTransferAccountToBeUsed == null) {
            val destinationAccountUserResult = restTemplate.postForEntity(
                USERS_BASE_SERVICE_PATH,
                UserDTO(
                    name = "ANOTHER_NAME",
                    surname = "ANOTHER_SURNAME",
                    docType = NATIONAL_ID,
                    document = "THIRD_DOCUMENT",
                    docCountry = "ANOTHER_DOC_COUNTRY"
                ),
                RestResponse::class.java
            )

            destinationAccountUserResult.statusCode shouldBeEqual HttpStatus.OK
            THIRD_RANDOM_USER_ID =
                UUID.fromString(((destinationAccountUserResult.body!!.data as Map<*, *>)["id"] as String))

            val destinationAccountResult = restTemplate.postForEntity(
                "${ACCOUNT_BASE_SERVICE_PATH}/user/${THIRD_RANDOM_USER_ID}/agency/$AGENCY",
                null,
                RestResponse::class.java
            )

            destinationAccountResult.statusCode shouldBeEqual HttpStatus.OK

            destinationTransferAccountToBeUsed =
                Account(
                    id = (destinationAccountResult.body!!.data as Map<*, *>)["id"] as String,
                    agency = (destinationAccountResult.body!!.data as Map<*, *>)["agency"].toString().toShort(),
                    year = (destinationAccountResult.body!!.data as Map<*, *>)["year"].toString().toShort(),
                    userId = UUID.fromString((destinationAccountResult.body!!.data as Map<*, *>)["userId"] as String),
                    balance = (destinationAccountResult.body!!.data as Map<*, *>)["balance"] as Double,
                    status = ActivationStatus.valueOf((destinationAccountResult.body!!.data as Map<*, *>)["status"] as String)
                )
        }

    }

    @And("the user has made the following transactions:")
    fun the_user_has_made_the_following_transactions(transactions: List<Map<String, String>>) {
        transactions
            .map { transactionParameter ->
                Transaction(
                    originAccountId = transactionParameter["Origin Account"]!!,
                    destinationAccountId = transactionParameter["Destination Account"],
                    amount = transactionParameter["Value"]!!.toDouble(),
                    type = TransactionType.valueOf(transactionParameter["Type"]!!),
                    accountBalanceCurrentValue = 0.0,
                    date = LocalDate.parse(transactionParameter["Date"]!!).atStartOfDay()
                )
            }.forEach { transaction ->
                result = restTemplate.postForEntity(
                    BASE_SERVICE_PATH,
                    transaction,
                    RestResponse::class.java
                )
            }
    }

    @When("the user makes a deposit of {string}")
    fun the_user_makes_a_deposit_of(transactionValue: String) {
        result = if (flowActiveUSer) {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = accountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = DEPOSIT
                ),
                RestResponse::class.java
            )

        } else {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = deactivatedAccountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = DEPOSIT
                ),
                RestResponse::class.java
            )
        }

    }

    @When("the user makes a withdraw of {string}")
    fun the_user_makes_a_withdraw_of(transactionValue: String) {
        result = if (flowActiveUSer) {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = accountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = WITHDRAW
                ),
                RestResponse::class.java
            )

        } else {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = deactivatedAccountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = WITHDRAW
                ),
                RestResponse::class.java
            )
        }

    }

    @When("the user makes a transfer of {string}")
    fun the_user_makes_a_transfer_of(transactionValue: String) {
        result = if (flowActiveUSer) {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = accountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = TRANSFER
                ),
                RestResponse::class.java
            )

        } else {
            restTemplate.postForEntity(
                BASE_SERVICE_PATH,
                Transaction(
                    originAccountId = deactivatedAccountToBeUsed?.id!!,
                    amount = transactionValue.toDouble(),
                    type = TRANSFER
                ),
                RestResponse::class.java
            )
        }
    }

    @When("the user gets the transaction history of the account {string} from {string} to {string}")
    fun the_user_gets_the_transaction_history_of_the_account_from_to(accountId: String, startDate: String, endDate: String?) {
        result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/account/${accountId}/start/$startDate?endDate=$endDate",
            RestResponse::class.java
        )
    }

    @When("the user gets the transaction history of the account {string} from {string}")
    fun the_user_gets_the_transaction_history_of_the_account_from(accountId: String, startDate: String) {
        result = restTemplate.getForEntity(
            "$BASE_SERVICE_PATH/account/${accountId}/start/$startDate",
            RestResponse::class.java
        )
    }

    @Then("the account balance is increased by {string}")
    fun the_account_balance_is_increased_by(string: String?) {
        result.statusCode shouldBeEqual HttpStatus.OK
        (result.body!!.data as Map<*, *>)["id"].shouldNotBeNull()
        Serenity.recordReportData().withTitle("Client Account Initial State").andContents(accountToBeUsed.toString())
        Serenity.recordReportData().withTitle("Transaction Final State").andContents(result.toString())
    }

    @Then("the account balance is decreased by {string}")
    fun the_account_balance_is_decreased_by(string: String?) {
        result.statusCode shouldBeEqual HttpStatus.OK
        (result.body!!.data as Map<*, *>)["id"].shouldNotBeNull()
        Serenity.recordReportData().withTitle("Client Account Initial State").andContents(accountToBeUsed.toString())
        Serenity.recordReportData().withTitle("Transaction Final State").andContents(result.toString())
    }

    @Then("the destination account balance is increased by {string}")
    fun the_destination_account_balance_is_increased_by(string: String?) {
        result.statusCode shouldBeEqual HttpStatus.OK
        (result.body!!.data as Map<*, *>)["id"].shouldNotBeNull()
        Serenity.recordReportData().withTitle("Client Account Initial State").andContents(accountToBeUsed.toString())
        Serenity.recordReportData().withTitle("Destination Account Initial State")
            .andContents(destinationTransferAccountToBeUsed.toString())
        Serenity.recordReportData().withTitle("Transaction Final State").andContents(result.toString())
    }

    @Then("the transaction operation fails")
    fun the_transaction_operation_fails() {
        Serenity.recordReportData().withTitle("Client Account Initial State")
            .andContents(deactivatedAccountToBeUsed.toString())
    }

    @Then("the transaction history is:")
    fun the_transaction_history_is(transactions: List<Map<String, String>>) {
        result.statusCode shouldBeEqual HttpStatus.OK


    }

    @And("the origin account balance is decreased by {string}")
    fun the_origin_account_balance_is_decreased_by(transactionValue: String) {
        accountToBeUsed?.balance?.shouldBeEqual(((result.body!!.data as Map<*, *>)["accountBalanceCurrentValue"] as Double) - transactionValue.toDouble())
    }

}