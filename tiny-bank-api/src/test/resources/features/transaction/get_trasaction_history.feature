Feature: Get Transaction History

  The transaction history is a list of all transactions made by the user in the bank. The history is a list of transactions
  that can be filtered by date and account. It is a read-only operation and it is used to show the user transactions
  made in the bank account for a specific time range.

  **As a** Tiny Bank client
  **I want to** get the transaction history of my bank account
  **So that I can** see all the transactions made in my account in a time range

  Rule: The transaction history must return the transactions made by the user in a time range

    Scenario: Get the transaction history of a user account
      Given the user has an active account in Tyny Bank
      And the account balance is "200.00"
      And the user has made the following transactions:
        | Date       | Value  | Type     | Origin Account | Destination Account |
        | 2021-01-01 | 100.00 | DEPOSIT  | 123            |                     |
        | 2021-01-02 | 200.00 | WITHDRAW | 123            |                     |
        | 2021-01-03 | 100.00 | TRANSFER | 123            | 987                 |
      When the user gets the transaction history of the account "123" from "2021-01-01" to "2021-01-03"
      Then the transaction history is:
        | Date       | Value  | Type     | Origin Account | Destination Account | Balance Position |
        | 2021-01-01 | 100.00 | DEPOSIT  | 123            |                     | 300.00           |
        | 2021-01-02 | 200.00 | WITHDRAW | 123            |                     | 100.00           |
        | 2021-01-03 | 100.00 | TRANSFER | 123            | 987                 | 0.00             |

    Scenario: Get the transaction history of a user account without end date
      Given the user has an active account in Tyny Bank
      And the account balance is "200.00"
      And the user has made the following transactions:
        | Date       | Value  | Type     | Origin Account | Destination Account |
        | 2021-01-01 | 100.00 | DEPOSIT  | 123            |                     |
        | 2021-01-02 | 200.00 | WITHDRAW | 123            |                     |
        | 2021-04-03 | 100.00 | TRANSFER | 123            | 987                 |
      When the user gets the transaction history of the account "123" from "2021-01-01"
      Then the transaction history is:
        | Date       | Value  | Type     | Origin Account | Destination Account | Balance Position |
        | 2021-01-01 | 100.00 | DEPOSIT  | 123            |                     | 300.00           |
        | 2021-01-02 | 200.00 | WITHDRAW | 123            |                     | 100.00           |