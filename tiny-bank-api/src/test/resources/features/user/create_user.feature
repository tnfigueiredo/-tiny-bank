Feature: Create User

  To be able to be a client of the Tiny Bank, the user must register and create an account with the bank.

  **As a** person that wants to become a Tiny Bank client
  **I want to** provide my registration information
  **So that I can** have an active bank account

  Rule: When the person never had registered in the Tiny bank as a client it is needed all the registration information.This person must have a unique document from a specific type and country, otherwise it is considered someone already registered.


      Scenario: Create an account successfully
        Given a client with name "Jhon", surname "Doe", document type "NATIONAL_ID", document "abcdefg", country "PT"
        When the account creation is requested
        Then the client's register and account are created successfully

      @pending
      Scenario: Fail to create an account with duplicated national ID
        Given a client with name "Jhon", surname "Doe", document type "NATIONAL_ID", document "abcdefg", country "PT"
        And there is a client with the document type "National ID", document "abcdefg", country "PT"
        When the account creation is requested
        Then the client's register and account are denied

  Rule: When the person is a client with already existing national document registered in Tiny Bank, the client's account can be reactivated

    @pending
    Scenario: Reactivate an account successfully
      Given a client with name "Jhonny", surname "Doe", document type "NATIONAL_ID", document "123465", country "PT"
      And there is a client with the document type "National ID", document "123465", country "PT"
      When the account activation is requested
      Then the client's account is activated