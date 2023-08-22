# Requirements - OP26SEM52
## 1.  Functional Requirements
### 1.1 Must Haves
* The user should have a student house;
* The user should be able to view his or her credit amount;
* The user must be able to consume a certain number of portions of the food that is registered in the system;
* The user must be able to enter new food he or she bought into the system, specifying the costs and number of portions;
* After consuming a product or buying food, the user should see a respective decrease or increase to his or her credits;
### 1.2 Should Haves
* When a user has accidentally entered incorrect information into the system, he or her should be able to fix this mistake by inputting a negative quantity.
* The user needs to get a notification when his or her balance drops below negative 50 credits;
* A user can leave his or her house;
* All members of a student house can add a user to their student house;
* The user should be able to see a history of actions (consuming or buying food);
* The user should be able to reset the system so everyone is back at zero credits again and the system contains no food;
* An authenticated user should be able to inspect his/her balance (positive, negative credits) and the current overview of the products (and their portion);
* A finished (or spoiled) product should not be able to get consumed by anyone, so no one can accidently click it;
* If a product spoils, the remaining costs should be divided over all housemates;
### 1.3 Could Haves
* If multiple people are eating together, food and credits should be split equally;
* The program could have an automatic way of keeping track, if a product is spoiled;
* If multiple people are eating together, food and credits should be split equally.
### 1.4 Would/Won’t Haves
* A person can have multiple student houses;
* There are other roles (e.g. system admin, house owner, etc.);
* Products shall be remembered and can be selected from a list, although they are not present in the house.
## 2. Non-Functional Requirements
* The program shall be implemented in Java, using the Spring Framework and Gradle;
* Users must authenticate themselves with a unique username and password;
* The implementation should have at least 75% of meaningful line test coverage;
* The system should be scalable, which is achieved by applying modularity;
* The data of the User should be secured by a password;
* A first fully working version of the program shall be delivered at Friday, December 18th.