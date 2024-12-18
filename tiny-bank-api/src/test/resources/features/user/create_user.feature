Feature: Create User

  To be able to be a client of the Tiny Bank, the user must register and create an account with the bank.

  **As a** person that wants to become a Tiny Bank client
  **I want to** provide my registration information
  **So that I can** create an account

  Rule: When the person never had registered in the Tiny bank as a client it is needed all the registration information.This person must have a unique document from a specific type and country, otherwise it is considered someone already registered.

      @pending
      Scenario: Create an account successfully
        Given a new client with name "Jhon", surname "Doe", document type "National ID", document "abcdefg", country "PT"
        And there is no client with this document type and document identification
        When the account creation is requested
        Then the client's register and account are created successfully

      @pending
      Scenario: Create an account with duplicated national ID
        Given a new client with name "Jhon", surname "Doe", document type "National ID", document "abcdefg", country "PT"
        And there is a client with the document type "National ID", document "abcdefg", country "PT"
        When the account creation is requested
        Then the client's register and account are denied
