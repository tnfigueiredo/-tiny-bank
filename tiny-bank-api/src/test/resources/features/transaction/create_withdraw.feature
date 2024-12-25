Feature: Create Withdraw

  The withdraw transaction is a debit to the account balance that is created when a user makes a withdraw. The withdraw
  must be a transaction with value lower than zero and it only needs the origin account (in this case is the account
  addressed to apply the withdraw operation, not the account which money is being sent to).

  **As a** Tiny Bank client
  **I want to** withdraw a value from a bank account
  **So that I can** debit value from my account balance

  Rule: For doing a withdraw transaction the user account must be active

    Scenario: Fails to create a withdraw to an inactive account
      Given the user has a deactivated account in Tyny Bank
      When the user makes a withdraw of "-100.00"
      Then the transaction operation fails

  Rule: A withdraw to a user account must be a value greater than zero

    Scenario: Create a withdraw of a value greater than zero
      Given the user has an active account in Tyny Bank
      And the account balance is "200.00"
      When the user makes a withdraw of "-100.00"
      Then the account balance is decreased by "100.00"

    Scenario: Fails to create a withdraw of a value less than zero
      Given the user has an active account in Tyny Bank
      When the user makes a withdraw of "100.00"
      Then the transaction operation fails