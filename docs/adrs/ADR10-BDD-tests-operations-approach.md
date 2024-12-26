# ADR 10: BDD tests navigation and operations handling

## Context and Problem Statement

In order to allow validation on the BDD tests, it was necessary to choose an approach on how to trigger the API operations.

## Decision Outcome

To handle the test cases for BDD, it was chosen to trigger operations against teh application endpoints. To work in this 
direction it was chosen the RestTemplate component integrated with the Spring context. This component has some limitations 
for not allowing read the response for some of its methods. This brought an extra effort to handle and chain operations' 
call.