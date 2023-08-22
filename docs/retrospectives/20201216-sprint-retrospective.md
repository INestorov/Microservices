|  Person |                                        Task#                                       |   Assigned to   | Estimated Effort  per Task | Actual Effort  per Task | Done |                                              Notes                                              |
|:-------:|:----------------------------------------------------------------------------------:|:---------------:|:--------------------------:|:-----------------------:|:----:|:-----------------------------------------------------------------------------------------------:|
| Jorian  | Implement last functionalities and do some testing                                 | Jorian          | 3 hours                    | 4 hours                 | Yes  | registerUser, addHousemate, getHousemates and fixed some issues that arose because I wasn't wary enough of minor mistakes that I made, which made it last longer than expected    |
| Waded   | Testing Foodmanagement                                                             | Nina, Waded     | 10 hours                   | 0 hours                 | No   |                                                                                                 |
|         | Add additional methods foodmanagments (including reset, putting back portions etc) |                 | 5 hours                    | 7 hours                 | Yes  |                                                                                                 |
|         | Finishing Storage                                                                  | Waded, Nina     | 0 hours                    | 3 hours                 | Yes  | Together with Nina, spend quite some time trying to bug fix until deciding to do it differently |
| Nina    | Implementing Storage                                                               | Nina            | 2 hours                    | 7 hours                 | Yes  | with help of Waded, tried an unfunctional implementation first, therefore the hickup            |
|         | Testing Foodmanagement                                                             | Nina, Waded     | 10 hours                   | 2 hours                 | No   |                                                                                                 |
|         | Finishing Spoiled Food                                                             | Nina            | 1 hour                     | 1 hour                  | Yes  |                                                                                                 |
| Simcha  | Some work on documentation                                                         |                 | 0.5 hours                  | 0.5 hours               | Yes  |                                                                                                 |
|         | Integration test for authentication MS                                             |                 | 4 hours                    | 5 hours                 | Yes  | w/ help of Dimitar                                                                              |
| Ivan    | Test the api-gateway component                                                     | Ivan, Dimitar   | 5 hours                    | 7 hours                 | Yes  |                                                                                                 |
| Dimitar | Test the auth. microservice                                                        | Dimitar, Simcha | 10 hours                   | 7 hours                 | Yes  | I tested the service classes and the database interaction                                       |
|         | Test the api-gateway component                                                     | Dimitar, Ivan   | 10 hours                   | 10 hours                | Yes  | I tested half of the service classes and half the controllers.                                  |


## Main Problems Encountered:						
#### Problem 1:						
Description:  How to address some ambiguous functional requirements?						
Reaction: Asking out TA to elaborate on those requirements in order to decide how to implement them. As a result, we knew what to do.						
						
#### Problem 2:						
Description: How to work with MockMVC to test the authentication microservice						
Reaction: A lot of Googling and working together (Dimitar and Simcha).						
						
#### Problem 3:						
Description: We tried to implement Storage as a separate class, however struggled with the ManyToOne relationship and its implications over the microservice 						
Reaction: Instead we handle the StorageId as a parameter in the FoodProduct.						
									