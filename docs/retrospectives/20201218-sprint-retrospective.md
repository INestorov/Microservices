|  Person |                    Task                    | Task Assigned to | Estimated Effort  per Task | Actual Effort  per Task |                  Done                  |                                          Notes                                         |
|:-------:|:------------------------------------------:|:----------------:|:--------------------------:|:-----------------------:|:--------------------------------------:|:--------------------------------------------------------------------------------------:|
| Jorian  | Testing House Management                   | Jorian           | 7 hours                    | 9 hours                 | Yes                                    |                                                                                        |
|         | Fixing some issues in House Management     | Jorian           | 0 hours                    | 2 hours                 | Yes                                    |                                                                                        |
| Dimitar | Testing additional API-Gateway branches.   | Dimitar          | 1 hour                     | 1 hour                  | Yes                                    |                                                                                        |
|         | Add new features to the API-Gateway        | Dimitar          | 1 hour                     | 1 hour                  | Yes                                    |                                                                                        |
|         | Fix bugs in the API Gateway                | Dimitar          | 1 hour                     | 1 hour                  | Yes                                    |                                                                                        |
| Nina    | Testing Foodmanagement                     | Nina, Waded      | 4 hours (just for me)      | 8 hours                 | Yes                                    |                                                                                        |
| Waded   | Add Splitting credits functionality        | Waded            | 2 hours                    | 6 hours                 | Yes                                    | Struggled with getting the HTTP request to work at first.                              |
|         | Testing Foodmanagement                     | Nina, Waded      | 4 hours (just for me)      | 7 hours                 | Yes                                    | Decided to not test the HTTP request using unit testing, since it was too complicated. |
| Simcha  | Work on gitlab pipelines                   | Simcha           | 0.5 hours                  | 0.5 hours               | Yes                                    | Not on dev/master yet because of possible docker issues                                |
|         | Live testing and fixing code using postman | Simcha           | -                          | 6 hours                 | Can you ever be done with such a thing (: |       some collaboration w/ Dimitar                                                                                 |
| Ivan    | Fixing checkstyle issues.                  | Ivan             | 0.5 hours                  | 0.5 hours               | Yes                                    |                                                                                        |

## Main Problems Encountered:	
#### Problem 1:	
Description: 	Struggled with testing the HTTP, within gitlab
Reaction: 	Choose not to do it, as Postman tests were successful

#### Problem 2: 						
Description: Some problems arose when I added a transient keyword to variables in User class because I had a wrong method name						
Reaction: After struggling to find out the problem (the problem was described as missing accessors although I had getters and setters for everything, but I had to just change a method name from join to set			