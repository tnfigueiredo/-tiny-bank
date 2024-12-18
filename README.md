# Tiny Bank 2.0

The main goal of this README.md doc is to provide instructions on how to run the project and how the repository
is structured. The main idea was to separate the design documentation, the test documentation, and the project
implementation. As a starting point to guide the reasoning presented here was the non-functional requirement:

    Please try to keep it simple. The objective is to understand your approach to problems and your thought process 
    rather than a test of your technical knowledge, even if it means having to make trade-offs.

    This means that it's ok (and expected) to either skip or keep to a minimum certain aspects that would otherwise 
    be necessary in a production-ready application, such as:
    * authentication/authorisation
    * error handling
    * logging / monitoring
    * transactions
    
    (Feel free to cut down as much as you need to fit the solution into the time you have available)

Based on this statement I decided to take some time to write some scratches for the requirements and to register 
design decisions. I'll try to follow a BDD and Agile Modeling approach for that. The idea is to keep documentation as 
simple as possible, and also describe the requirements trying to follow a testing approach that can allow me to validate 
the requirements for the proposed features.

To allow me to bring more visibility to the approach and reasoning applied, I'll try to follow a "baby steps" commit 
approach. This will make it possible to have an idea of the whole process related to the progress of the activity.

## Features

 - Creation and deactivation of users
 - Ability for users to deposit/withdraw money from their accounts
 - Ability for users to transfer money to another user's account
 - View account balances
 - View transaction history

## Project structure

To make it easier to have a full running environment it was chosen a docker-compose structure. This will allow me to 
have a separate running environment for the documentation, a separate running environment for the BDD tests, and the 
running application itself. Those are the services running in this docker-compose:

 - appdocs: it is a MkDocs service based on a Docker image that generate html documentation based on markdown files.
 - testdocs: it is a httpd image that makes available HTML documentation generated through the project's build test result.
 - tiny-bank-api: it is the RESTful API service related to the project requirements. This project is accessible through an 
OpenAPI HTML interface.

This decision was taken to allow navigating the project documentation and implementation having as the starting point
the project running environment itself. This can provide a more interactive environment, and also allow an approach in 
which documentation and source code belong to a unified deliverable environment.

## How to run it

to run the project locally, it is necessary to have installed only [Docker](https://docs.docker.com/engine/install/) and 
[docker-compose](https://docs.docker.com/compose/install/). On the project root level it is just to run "docker-compose up" 
and the 3 mentioned services are available. To access the services it is just to open the following addresses in your 
browser:

 - appdocs. http://localhost:8000
 - testdocs: http://localhost:8081
 - tiny-bank-api: http://localhost:8080/tiny-bank/api/swagger-ui/index.html

If it is necessary to modify, build, and run the Kotlin project locally 
it is necessary to [install Java 17](https://openjdk.org/projects/jdk/17/) and an IDE of your choice.


## Reference Documentation

For further reference, please consider the following sections:

 * [Cloning a repository](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)
 * [MkDocs.org](https://www.mkdocs.org/)
 * [Agile Modeling](https://agilemodeling.com/)
 * [Living Documentation](https://serenity-bdd.github.io/docs/reporting/living_documentation)