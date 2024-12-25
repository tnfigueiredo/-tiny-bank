Feature: Deactivate User

  The Tiny User Bank can at any moment choose to close its account in the Tiny Bank. The user is not deleted from the bank
  data records. It is deactivated to allow future data verification. The user deactivation and user account deactivation
  are done in the same operation.

  **As a** client that has an account at Tiny Bank
  **I want to** request the account deactivation
  **So that I can** end my relationship with the bank

  Rule: When a Tiny Bank client request to deactivate its account, based on the clients national ID the deactivation is done.

    Scenario: Deactivate an account for a national ID having no bank account
      Given the client identification for document type "NATIONAL_ID", document "34khj543k", country "PT"
      And there is a client with this document information
      And there is no client account
      When the user deactivation is requested
      Then the client's account is deactivated

    Scenario: Deactivate an account for a national ID having a bank account
      Given the client identification for document type "NATIONAL_ID", document "34khj543k", country "PT"
      And there is a client with this document information
      And there is a client account
      When the user deactivation is requested
      Then the client's account is deactivated

    Scenario: Deactivate an account for a non existing national ID
      Given a client with name "Jhon", surname "Doe", document type "NATIONAL_ID", document "12343532", country "PT"
      And there is no client with this document information
      When the user deactivation is requested
      Then the client's account deactivation is denied