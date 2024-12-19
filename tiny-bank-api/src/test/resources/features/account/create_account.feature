Feature: Create User Bank Account

  For a client to be able to do banking transactions as a Tiny Bank client, he needs to have an account.

  **As a** Tiny Bank client
  **I want to** create a bank account
  **So that I can** do bank transactions through Tiny Bank

  Rule: The user can create a bank account if he is already registered

    Scenario: Create an account successfully
      Given a client for account creation with document identification: document type "NATIONAL_ID", document "abcdefgaga", country "PT"
      And the client requesting the account creation has its register active
      When the account creation is requested
      Then the client's account is created successfully

    Scenario: Fails to create an account to user with invalid document information
      Given a client for account creation with document identification: document type "NATIONAL_ID", document "03485203948", country "PT"
      And the document for the client requesting the account creation is invalid
      When the account creation is requested
      Then the client's account creation is denied