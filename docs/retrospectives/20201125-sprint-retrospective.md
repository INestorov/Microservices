| Person  | Task                                                                    | Task Assigned to | Estimated Effort | Actual Effort | Done | Notes                                                                      |
| ------- | ----------------------------------------------------------------------- | ---------------- | ---------------------------- | ------------------------- | ---- | -------------------------------------------------------------------------- |
| Jorian  | Design data model                                                       | Ivan, Jorian     | 30 min                       | 1 hr                      | Yes  | Ivan and I both created a data model for discussion purposes               |
|         | Build data model in SQL workbench                                       | Ivan, Jorian     | 30 min                       | 45 min                    | Yes  |                                                                            |
| Dimitar | Connect the app to the db                                               | Dimitar          | 5 min                        | 30 min                    | Yes  | Needs a .properties file (and possibly a configuration class)              |
|         | Set up a test database.                                                 | Dimitar          | 5 min                        | 30 min                    | Yes  | Needs only a @DataJpaTest annotation to set up an in-memory db.            |
|         | Create tests for the model classes.                                     | Dimitar          | 2 hours                      | 30 min                    | No   | I created only a base test class.                                          |
|         | Implement House and Storage classes                                     | Dimitar          | 30 minutes                   | 30 minutes                | Yes  | My work was incomplete and Simcha finished it.                             |
| Nina    | Implement User class + tests                                            | Nina             | 1.5 hrs                      | 2 hrs                     | Yes  |                                                                            |
| Waded   | Implement FoodProduct entity + tests                                    | Waded            | 1.5 hrs                      | 2.5 hrs                   | Yes  | Struggled a lot with build failures, now know how to debug those failures. |
| Simcha  | Requirements document                                                   | Simcha           | 1hr                          | 1hr                       | Yes  | Gonna finish this this evening                                             |
|         | Authentication files                                                    | Simcha, ..       | 1hr                          | 1hr                       | No   | To be continued                                                            |
|         | Changes to entity classes and adding javadoc and checkstyle refactoring | Simcha           | 1.5-2hr                      | 1.5-2hr                   | Yes  |                                                                            |
| Ivan    | Design data model                                                       | Ivan, Jorian     | 30 min                       | 20min                     | Yes  |                                                                            |
|         | Design data model with MySql WorkBench                                  | Ivan, Jorian     | 30 min                       | 1h                        | Yes  |                                                                            |
|         | Added annotation to each class                                          | Ivan             | 15 min                       | 1h 30 min                 | Yes  |                                                                            |

## Main Problems Encountered:			
#### Problem 1:			
Description:  Consensus on what would the database schema be.			
Reaction: Some team members made a compromise and agreed with their other teammates' suggestion.			
			
#### Problem 2:			
Description: FoodMicroservice had some hickups with the build. 			
Reaction: Ask a group member for help and fixed it efficiently.			