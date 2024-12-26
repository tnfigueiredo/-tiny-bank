Feature: Get Account Balance

  The account balance is the amount of money that the user has in the account. The balance is the result of the sum of all
  transactions that the user has made in the account. In Tiny Bank scope balance is always positive or zero, due to
  simplification scope.

  **As a** Tiny Bank client
  **I want to** get the balance of a bank account
  **So that I can** know how much money I have in the account

  Rule: There is possible to get the balance of an existing account

    Scenario: Get the balance of an existing account
      Given the user with document type "NATIONAL_ID", document "23052783", country "PT" has an active account in Tyny Bank
      When the user gets the account data and balance
      Then the account balance has some value