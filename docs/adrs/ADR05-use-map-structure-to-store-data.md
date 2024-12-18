# ADR 05: Use Map Structures to Store Data

## Context and Problem Statement

In order to attend the requirement of not using a full-fledged database, it is necessary to choose a data structure to 
store data in the project.

## Considered Options

1. Use a Map and some random number object as a key.
2. Use a Map structure and some rando UUID object as a key.
3. Use a Dictionary and some random number object as a key.
4. Use a Dictionary and some rando UUID object as a key.

## Decision Outcome

The option 2 was the chosen one. Those Maps will be handled in an object with a repository responsibility. 
Objects will have their relationships will be organized through the mapping keys in the Map structure. This will allow 
to have a current state of the entities related to the application domain. To store information that needs track of 
historical data it will be followed a strategy to store events in the Map structure.

This brings 2 good things related to the requirements proposed:

- If it is needed to restore the current state of the project entities, a good way to go is to replay the events
  processing.
- The implementation of features that have the nature of behaving like events comes for free.