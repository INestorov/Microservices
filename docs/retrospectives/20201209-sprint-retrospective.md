|  Person |                              Task                               |                    | Estimated Effort  per Task | Actual Effort  per Task | Done |                                            Notes                                            |
|:-------:|:---------------------------------------------------------------:|:------------------:|:--------------------------:|:-----------------------:|:----:|:-------------------------------------------------------------------------------------------:|
| Jorian  | Finish House Microservice (logging, testing, last methods)      | Jorian             | 4 hours                    | 6 hours                 | Yes  |                                                                                             |
|         | Upload requirements to Gitlab                                   | Jorian             | 1 hour                     | 1 hour                  | Yes  | Obviously not very challenging, very therapeutic though                                     |
| Nina    | Finish Credits                                                  | Nina and Waded     | 1 hours                    | 20 minutes              | No   | Needs some functionality included in the food functionalities                               |
|         | Finish all food functionalities                                 | Nina and Waded     | 4 hours                    | 2 hours                 | No   | Most of it is implemented, however some methods are still missing                           |
|         | Add Storage                                                     | Nina and Waded     | 2 hours                    | 3 hours                 | No   | We run into issues displaying the Storage                                                   |
| Waded   | Finish Credits                                                  | Nina and Waded     | 1 hour                     | 20 minutes              | No   | Needs some functionality included in the food functionalities                               |
|         | Finish all food functionalities                                 | Nina and Waded     | 4 hours                    | 3 hours                 | No   | Mostly done, we ran into some issues of missing methods which still need to be implemented. |
| Dimitar | Create a documentation for components                           | Dimitar            | 4 hours                    | 3 hours                 | Yes  | There is now documentation for the authentication microservice and the API Gateway          |
|         | Write tests for the API Gateway                                 | Dimitar and Ivan   | 10 hours                   | 2 hours                 | No   |                                                                                             |
|         | Write tests for the auth microservice                           | Dimitar and Smcha  | 15 hours                   | 3-4 hours               | No   | The intregration between components of the authentication is what is left to be tested      |
|         | Fix problems  in the authentication ms.                         | Dimitar and Simcha | 1 hour                     | 1 hour                  | Yes  |                                                                                             |
| Ivan    | Write tests for the API Gateway                                 | Ivan, Dimitar      | 10 hours                   | 1 hour                  | Yes  |                                                                                             |
| Simcha  | Setup database for users                                        |                    | 1 hour                     | 1 hour                  | Yes  |                                                                                             |
|         | Documentation to git and write documentation for authentication |                    | 2.5 hours                  | 2.5 hours               | Yes  |                                                                                             |
|         | Work on user validator and test fixes for the class             |                    | 1 hour                     | 1 hour                  | Yes  |                                                                                             |
|         | AuthenticationController tests                                  |                    | 3 hours                    | 2 hours                 | Yes  | Need to run coverage on these tests (:                                                      |

## Main Problems Encountered:						
#### Problem 1:						
Description:  Deciding on how the documentation is to be structured.						
Reaction:  Consulting a professional and asking him to give suggestions for the structure. Consequently, the documentation had a very nice chosen structure and looked very clear.						
#### Problem 2:						
Description: Testing the API Gateway endpoints.						
Reaction: Making unit tests by mocking the connection with other application components in order to only test if the Gateway endpoints act right according to returned responses from other components.						
#### Problem 3:	Waded, Nina					
Description: We lost overview of all the functionalities and methods we needed to implement. Leaving us stuck on how to continue.						
Reaction: We sat down together and went through the list of requirements, creating a list of methods. 						
#### Problem 4: 	Waded, Nina					
Description: Figured out we had no good way of keeping track of the price of a foodproduct if some portions had already been taken.						
Reaction: Changed the field "price" to "pricePerPortion" to get rid of this issue.						