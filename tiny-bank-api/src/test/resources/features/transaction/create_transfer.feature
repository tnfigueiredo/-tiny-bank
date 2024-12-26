Feature: Create Transfer

  The transfer transaction is a credit to the account balance in the account origin and a debit in the account destination.
  The transfer must be a transaction with value greater than zero and it needs the origin account and the destination account.

  **As a** Tiny Bank client
  **I want to** transfer a value to a destination bank account
  **So that I can** debit value to my account balance and credit value to the destination account balance

  Rule: For doing a transfer transaction the origin and destination accounts must be active

    Scenario: Fails to create a transfer to an inactive origin account
      Given the user has a deactivated account in Tyny Bank
      And the destination account "987" is chosen for transfer
      When the user makes a transfer of "100.00"
      Then the transaction operation fails

  Rule: A transfer to a user account must be a value greater than zero

    Scenario: Create a deposit of a value greater than zero
      Given the user has an active account in Tyny Bank
      And the destination account "987" is chosen for transfer
      When the user makes a deposit of "100.00"
      Then the destination account balance is increased by "100.00"
      And the origin account balance is decreased by "100.00"

    Scenario: Fails to create a transfer of a value less than zero
      Given the user has an active account in Tyny Bank
      And the destination account "987" is chosen for transfer
      When the user makes a transfer of "-100.00"
      Then the transaction operation fails