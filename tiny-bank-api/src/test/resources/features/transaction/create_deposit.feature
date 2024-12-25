Feature: Create Deposit

  The deposit transaction is a credit to the account balance that is created when a user makes a deposit. The deposit
  must be a transaction with value greater than zero and it only needs the origin account (in this case is the account
  addressed to apply the deposit operation, not the account which money is being sent to).

  **As a** Tiny Bank client
  **I want to** deposit a value in a bank account
  **So that I can** add value to my account balance

  Rule: For doing a deposit transaction the user account must be active

    Scenario: Fails to create a deposit to an inactive account
      Given the user has a deactivated account in Tyny Bank
      When the user makes a deposit of "100.00"
      Then the transaction operation fails

  Rule: A deposit to a user account must be a value greater than zero

    Scenario: Create a deposit of a value greater than zero
      Given the user has an active account in Tyny Bank
      When the user makes a deposit of "100.00"
      Then the account balance is increased by "100.00"

    Scenario: Fails to create a deposit of a value less than zero
      Given the user has an active account in Tyny Bank
      When the user makes a deposit of "-100.00"
      Then the transaction operation fails
