Feature: Create User Bank Account

  For a client to be able to do banking transactions as a Tiny Bank client, he needs to have an account. For simplification
  purposes, there will be only one account per client. The account creation is done based on the client's national ID. Those
  test scenarios take in consideration that based on the client's national ID the user information is recovered, and then
  the account is created.

  **As a** Tiny Bank client
  **I want to** create a bank account
  **So that I can** do bank transactions through Tiny Bank

  Rule: The user can create a bank account if he is already registered

    Scenario: Create an account successfully
      Given a client for account creation with document identification: document type "NATIONAL_ID", document "03485203948", country "PT"
      And the client requesting the account creation has its register active in the agency "0001"
      When the account creation is requested
      Then the client's account is created successfully

    Scenario: Fails to create an account to user with invalid document information
      Given a client for account creation with document identification: document type "NATIONAL_ID", document "03485203948", country "PT"
      And the document for the client requesting the account creation is invalid
      When there is no user with the document information
      Then the client's account is not created

  Rule: The user that already has an account cannot create a new account

    Scenario: Fails to create an account to user that already has an account
      Given a client for account creation with document identification: document type "NATIONAL_ID", document "03485203948", country "PT"
      And the client requesting the account creation has its register active in the agency "0001"
      And the client's account already exists
      When the account creation is requested
      Then the client's account is not created