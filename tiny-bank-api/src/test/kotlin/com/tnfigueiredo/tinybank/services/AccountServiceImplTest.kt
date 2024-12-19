package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.model.Account
import com.tnfigueiredo.tinybank.repositories.AccountRepository
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@SpringBootTest
internal class AccountServiceImplTest {

    private companion object{
        val A_RANDOM_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
        val AN_AGENCY: Short = "1".toShort()
        val AN_AGENCY_AS_STRING = String.format("%04d", AN_AGENCY)
        val AN_YEAR = "2024".toShort()
        val A_LATEST_ACCOUNT = "${AN_YEAR}${AN_AGENCY_AS_STRING}0002"
    }

    @MockitoBean
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var accountService: AccountService

    @Test
    fun `when there is no account for the agency in the current year`() {
        val accountToBeSaved = Account(
            id = "${AN_YEAR}${AN_AGENCY_AS_STRING}0000",
            agency = AN_AGENCY,
            userId = A_RANDOM_ID,
            balance = "0.0".toDouble(),
            year = AN_YEAR
        )

        Mockito.`when`(accountRepository.findLatestAccount("$AN_YEAR$AN_AGENCY_AS_STRING")).thenReturn(null)
        Mockito.`when`(accountRepository.saveAccount(accountToBeSaved)).thenReturn(Result.success(accountToBeSaved))
        val result = accountService.createAccount(A_RANDOM_ID, AN_AGENCY, AN_YEAR).getOrNull()!!

        result shouldBeEqual accountToBeSaved

    }

    @Test
    fun `when there is an account for the agency in the current year`() {
        val accountToBeSaved = Account(
            id = "${AN_YEAR}${AN_AGENCY_AS_STRING}0003",
            agency = AN_AGENCY,
            userId = A_RANDOM_ID,
            balance = "0.0".toDouble(),
            year = AN_YEAR
        )

        Mockito.`when`(accountRepository.findLatestAccount("$AN_YEAR$AN_AGENCY_AS_STRING")).thenReturn(A_LATEST_ACCOUNT)
        Mockito.`when`(accountRepository.saveAccount(accountToBeSaved)).thenReturn(Result.success(accountToBeSaved))
        val result = accountService.createAccount(A_RANDOM_ID, AN_AGENCY, AN_YEAR).getOrNull()!!

        result shouldBeEqual accountToBeSaved
    }

}