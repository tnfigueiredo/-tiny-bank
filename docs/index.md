# Tiny Bank 2.0

This small documents session aims to state the documentation needed to make clear the approach used for the Tiny 
Bank 2.0 in terms of design and decisions. To reach the goal to simplify the project documentation it was chosen as a 
design approach the usage of C4-Model diagrams. This decision was taken since C4-Model diagrams have a less strict 
rules' notation. It can simplify reading and communication with a broader audience.

Since the requirements asked to keep it simple and suggest you to use something like a dictionary or a map or a similar 
data structure, and not a full-fledged database, all the aspects that comes for free with a full-fledged database were 
left behind. It will be used Map structures to store the objects. Their relationships will be organized through the mapping 
keys in the Map structure. This will allow to have a current state of the entities related to the application domain. 
To store information that needs track of historical data it will be followed a strategy to store events in the Map 
structure. This brings 2 good things related to the requirements proposed:

 - If it is needed to restore the current state of the project entities, a good way to go is to replay the events 
   processing. 
 - The implementation of features that have the nature of behaving like events comes for free.

Also into this direction of keeping the implementation simple and being able of skip production ready features, another 
decision taken is that there will be no login and user access control issues. This can bring a complexity level that 
can impact tests and refactorings proposed to be done. In a first moment, concurrence aspects were not taken 
into consideration. If this scenario should be solved, services with centralized access and concurrence controls would be 
chosen to solve this issue. Or in case of a needed scenario, some specific implementation using thread-safe solutions.

## System Context

This context represents the idea of the application as a whole.

![System Context](./assets/system-context.svg)

