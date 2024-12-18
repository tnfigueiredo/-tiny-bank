# Tiny Bank 2.0

This small documents session aims to state the documentation needed to make clear the approach used for the Tiny 
Bank 2.0 in terms of design and decisions. To reach the goal to simplify the project documentation it was chosen as a 
design approach the usage of C4-Model diagrams.

Since the requirements asked to keep it simple and suggest you to use something like a dictionary or a map or a similar 
data structure, and not a full-fledged database, all the aspects that comes for free with a full-fledged database were 
left behind. It will be used Map structures to store the objects. Also into this direction of keeping the implementation 
simple and being able of skip production ready features, another decision taken is that there will be no login and user 
access control issues. This can bring a complexity level that can impact tests and refactorings proposed to be done. 
In a first moment, concurrence aspects were not taken into consideration. More details about those decisions in the ADRs.

## System Context

This context represents the idea of the application as a whole.

![System Context](./assets/system-context.svg)

