# ADR 08: Application Layers Structure

## Context and Problem Statement

In order to organize the application in layers and responsibilities it was necessary to choose an 
application layer structure.

## Decision Outcome

The layer structure chosen for this application was the standard common spring applications that uses 
controller/services/repositories. In this structure the controllers handles data input and data validation, the services 
handle business operations, and the repository handles data saving and recovering responsibilities. Some simplification was 
applied for not using a real database.

The application structure is also base on interfaces to allow being ok with the Liskov Substitution Principle, Interface Segregation Principle and 
Dependency inversion principle.