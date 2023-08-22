: SEM Group 52
#   API Gateway

## Overview
### Functionality
The API Gateway is a component used along with the Microservice architecture pattern.
The API Gateway is responsible for:
* Intercepting all client requests to the application
* Forwarding them to the right microservice accordingly.
* Creating the user sessions where temporary user data is stored during their 
  interaction with the application.
  

In addition, it provides:
* User sessions which store temporary user data during his/her interaction with the application.
* Partial authentication through the stored data in the user sessions.

### Software Dependencies
The authentication microservice is implemented using:
* The Java Programming Language - Version 11, for writing the application code
* The Spring Boot Java Framework:
    * Spring Boot - creating and running the server of the API Gateway component.
    * Spring Web MVC - creating the API endpoints of the microservice, which are HTTP request endpoints -
    * JUnit, Mockito and H2 DBMS - testing the component

* The Gradle build automation tool - for setting the dependencies of the API Gateway
  and enforcing Checkstyle and PMD rules
  

### Why do we need it?
The API Gateway application component abstracts the complexity of the microservice application from the user
by providing a single server address for client applications to communicates with. As a result, the Gateway hides
their existence from the user, thus allowing for the creation of more scalable applications.


## API Endpoints

### Request Chaining
The way the API Gateway forwards HTTP requests from the user is by first receiving the user request
and then sending a new HTTP request for the appropriate microservice, keeping the necessary information
from the user request like request body and request parameters, as well as almost the whole request path.

### User Sessions
*   The Gateway uses sessions to store data for every currently logged user of the application.
    When the user logs in successfully, his identifier is saved in the session storage according
    to the session cookie he is allocated. Also, a request is made to the appropriate microservice
    for other information to be saved in the session.
    
*   When the user wants to log out from the application, the session data is deleted.
    
*   All other requests the user makes to the Gateway are checked for containing the necessary
session data in the storage corresponding to the session cookie in the user's request. If the required
data is not found, the Gateway knows either the user is not logged in, in other words not 
authenticated, or his session has expired. In both cases, the user is notified about this and 
required to log in first. This ensures security for the application guarded by the API-Gateway
because a malicious user cannot break anything if he is not authenticated first. 

The sessions of a user can contain the following data:
-   His unique id in the application
-   The house id if the house he us currently a member of 

### User-Gateway Interaction Overview
When the user starts using the application, he should send either a
request for registration:
`http://host:port/application/authentication/sign-up` 

or logging: 
`http://host:port/application/authentication/login`

to the Gateway, which will forward them
to another microservice for authentication. If the registration is successful,
the user then has to log in. Every other initial request to the gateway will be rejected
with a `Bad Request 400` response:
```
Could not forward request: User is not logged in!
```
If a user is already logged in, but tries to send a request for log-in, the 
Gateway responds with a `Bad Request 400` response:
```
Could not forward request: User is already logged in!
```

#### Registration Procedure
* The user sends a request with his registration credentials in the request body. 
* The Gateway forwards the request to an authentication microservice.
* 1) The microservice returns an `OK 200` response containing the id of 
  the new user. 
     1) The Gateway sends a request for adding the new user to the house
            management microservice. 
         
     2) The Gateway sends a request for creating a credits account for the
            new user to the food management microservice.
         
     3) The Gateway sends an `ACCEPTED 202` HTTP response to the user,
    indicating that the registration was successful.
* 2) The microservice returns an error response, which is just forwarded by the Gateway.
    No other requests are made to microservices in this case.
    
#### Unregistering Procedure
* The user sends a request with his credentials in the request body.
* The Gateway checks if the user is logged in. If not, it sends an error response:
```
Could not forward request: User is not logged in!
```
* The Gateway forwards the request to an authentication microservice.
* 1) The microservice returns an `OK 200` response. Then the Gateway removes his
     current session data and forwards the authentication response to the user.
* 2) The microservice returns an error response, which is just forwarded by the Gateway.
  
#### Logging Procedure
* The user sends a request containing his credentials as request parameters.
* The Gateway checks to see if the user is already logged in, If he is, the Gateway returns
  a `Bad Request 400` response:
  ```
  Could not forward request: User is already logged in!
  ```
* The Gateway forwards the request to the authentication microservice.
* The microservice returns a response:
  * Successful logging response. The response should contain the id of the user in the body:
    1) The Gateway saves the user id in the user's session as an attribute.
    2) The Gateway requests the house id of the user's house ( if such exists ) by sending
      an HTTP request to the House Management Microservice.
    3) If House Management returns an HTTP response with status code of type success -
      200, 201, 202, then the house id is fetched successfully and the Gateway saves
       it as the second session attribute.
    4) The Gateway returns an HTTP response `ACCEPTED 202`:
    ```
    User was successfully logged in!
    ```
  * The logging was not successful. The HTTP response from the authentication microservice 
    contains an error message, which the Gateway just forwards to the user, also
    keeping the status code of the received HTTP response from authentication.

#### Logout Procedure
* The user sends a request for logout to `http://host:port/application/authentication`
* The Gateway checks if the user is logged in. If not, it sends an error response:
  ```
  Could not forward request: User is not logged in!
  ```
* The Gateway just deletes the session storage data of the user. After that, he will
not be recognised as logged in anymore.
* The Gateway returns an `OK 200` response:
  ```
  Session attribute deleted!
  ```

#### Forwarding other requests and responses
Apart from the aforementioned requests, the API Gateway uses a very 
universal way when dealing with received requests.
It performs the following actions:
1) Receives the request
2) Checks if the user is logged in. If not, it sends a `Bad Request 400` response like this:
```
Could not forward request: User is not logged in!
```

3) If the user is logged in, the gateway then creates a new request from the
received one, using the request body, parameters and path variables. It also
   appends any necessary user data as request parameters and changes the URL address
   of the new request so that it answers to the right microservice url. The last action is
   determined by the request path information:
   `/application/{ operation }/var1/var2/...`
   Depending on the operation word in the URL path, the Gateway knows to
   which microservice it should forward the request.
   
4) The Gateway sends the new  HTTP request to the service
5) The service responds. The Gateway just forwards the response to the user in the
response body and the same HTTP status.


## Possible Future Upgrades to the API-Gateway
