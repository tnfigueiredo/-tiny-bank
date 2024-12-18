# ADR 04: Use C4-Model and PlantUML for Diagrams

## Context and Problem Statement

In order to create diagrams to model project representation structures it was necessary to choose 
a modeling approach.

## Considered Options

1. External tool to create the diagrams and export the images using UML for Modeling.
2. External tool to create the diagrams and export the images using C4-Model for Modeling.
3. PlantUML to create the diagrams integrated to the project source code structure using UML for modeling.
4. PlantUML to create the diagrams integrated to the project source code structure using C4-Model for modeling.

## Decision Outcome

It was chosen the option 4. This decision was taken because C4-Model diagrams have a less strict rules' notation. It 
can simplify reading and communication with a broader audience.