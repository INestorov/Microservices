: SEM Group 52
#   Authentication Microservice

## Overview
### Functionality
This microservice is mainly responsible for:
* Storing user's credentials
* Allowing users to register/unregister on the application
* Allowing users to login in the application

In addition, it provides an API for:
* authenticating people by their usernames - meant for use only by other microservices
* resetting the user's database

Extra implemented business logic:
* username, email and password validation through the use of regex expressions.


**Note!** The microservice does not provide API for a logout operation or for changing a user's password.

### Software Dependencies
The authentication microservice is implemented using:
* The Java Programming Language - Version 11, for writing the application code
* The Spring Boot Java Framework:
  * Spring Boot - creating and running the server of the microservice
  * Spring Data JPA and Hibernate ROM - creating and persisting the database model
  * Spring Web MVC - creating the API endpoints of the microservice, which are HTTP request endpoints - 
  * Spring Security - securing the microservice and password encoding
  * JUnit, Mockito and H2 DBMS - testing the microservice
    
* The Gradle build automation tool - for setting the dependencies of the microservice 
  and enforcing Checkstyle and PMD rules

## Database Overview
### User Model
The User table contains the following information about each user:
1. User id, a number, unique for every user
2. Username, unique for every user
3. Email, unique for every user
4  Password, a list of characters
   
An example User entry in the table:
```
 {
    id: 1,
    usernames: xyzitsme,
    email: xyz@example.com,
    password: $2a$10$8yBT84WQUS2epmlj/41xbOJW2bKm5p9wjuhE92KYQPxBHKbRInh3a
 }
```

### Database Server
The database server uses a MySQL database management system.
It is located on one of the public educational TU Delft servers.


## API Endpoints

All API endpoints have a common path in their request mapping pattern: `/application/authentication`

#### Registration: `POST   '/sign-up'`
This endpoint allows people to register in the microservice by providing
their credentials. The endpoint expects that these credentials will be provided
by the new user in the body of the HTTP request in the following format:

```
{ 
  "username": The unique username of the new user  
  "email": The unique email of the new user
  "password": The password of the new user
}

## Note! - The user id mentioned earlier is automatically generated
by the application, so it should not be provided in the request. 
```

The registration procedure is as follows:
1. The request is received, and the body is checked for the required information. The regex patterns
   for the credentials are as follows:
   * The username should be between 8 and 24 characters
   * The username should contain only alphanumeric characters
   * The email should contain a @ sign.
   * The password should contain:
      * At least one lowercase letter
      * At least one uppercase letter
      * At least one digit
      * At least one of the symbols `@#$%^&-+=()`
   * The password should be between 8 and 24 characters long.
  
   If at least one of the credentials is missing, or if one of the credentials does not match the
   required regex pattern the microservice returns a `406 Not acceptable` response with the appropriate
   error message.
   
   **Possible error messages are:**
   ```
    Bad credentials: One of the provided credentials was missing.
   ```
   ```
    Bad credentials: Username should be between 8 and 24 characters.
   ```   
   ```
    Bad credentials: Password must contain at least one lowercase,
    and one uppercase character, as well as one digit and one of the characters
    @#$%^&-+=(). The password must be at least 8 characters long and shorter
    than 24 characters.
   ```
   
2. The provided password is encoded. Then the database is checked for already existing
users with the provided username or email. If yes, the microservice returns a `409 Conflict` response:
   ```
   Bad credentials: User with these credentials already exists!
   ```

3. The new user is saved in the database. If the operation was successful,
the user is notified with a `201 Created` response, containing the id of the 
   new user:
   ``` 
   1
   ```
    If the database persisting operation fails for some reason, the user is sent
    a `500 Internal server error` response containing the message of the error encountered.

#### Logging-in: `POST  '/login'`
This endpoint lets the user log in to the application by providing their credentials in an HTTP request. To this end, 
the user should input their username and password, as in the following format: 

```
{ 
  "username": The username of the user  
  "password": The password of the user
}
```

1. When a login request is received, the API gateway checks if the current session does not already have a user ID associated
with it. This can only be the case when the user is already logged in, in which case a `400 Bad Request` will be returned. 
Otherwise, the request is forwarded to the authentication microservice, which further processes the received
credentials. 

2. The authentication microservice verifies the credentials. At first, it searches the database for the username which
was inputted. If the username was not found, or if the password does not match the encrypted password saved in 
the database, an error message is returned. The microservice will return a `401 Unauthorized` response, which 
encapsulates an explanatory error message. If the user transmitted correct credentials, an `200 OK` response 
is returned. 

    Possible error messages which are forwarded at this stage are: 

    ```
    Bad credentials: Password incorrect.
    ```
    ```
    Bad credentials: Username does not exist.
    ```

3. The API gateway continues to process the response by the authentication microservice. If the login was successful,
it will set the user ID in the session, and fetch some user information like the credit balance. After this, a
sucess response is returned. In the case of any exceptions, it will catch the exception, and return an appropriate
error message.

#### Unregistering: `DELETE  '/unregister'` 
This endpoint is meant for users who want to unregister from the microservice, and the 
application it is part of. For this purpose, the endpoint expects the username and the 
password of the user to unregister. These should be contained in the request
as request parameters. 
###### Example:
    ``` 
    /application/authentication/unregister?username=Dimitar&password=Passssss
    ```

The microservice then goes the following steps:
1. It check if the username and password are present in the request as parameters.
If it does not find them, it sends a `400 Bad Request` response:
   ```
   Bad credentials: One of the unregister credentials was null.
   ```
   
2. It checks of a user exists in the database with the provided credentials.
    If it finds one, it then encodes the provided password and compares it with stored
   one. 
    If the user is not found, or the passwords does not match, the service sends a 
   `400 Bad Request` response indicating the problem:
   ```
   Bad credentials: Could not unregister user with the provided credentials!
   ```
   
3. If the user is found and the passwords match, the service tries to delete the user
   from the database. If the operation did not throw an exception, the service returns
   an `200 OK` response. If an exception was thrown, the service returns a `500 Internal server error`
   response:
   ```
   User was successfully unregistered!
   ```
   ```
   Bad credentials: Could not unregister user with the provided credentials!
   ```
   
##### Authenticating Users: `POST  '/user/get_names'`
This request is intended for usage only by another microservice
that wants to authenticate a list of users by their usernames.
The endpoint expects the list of usernames to be provided in the 
body of the request in the following JSON format:
   ```
    [
        "username1",
        "username2",
        ...
    ]   
   ```

The microservice then performs the following steps:

1. It checks if the list is not in the body or if
it is empty. If yes, then it sends a `400 Bad Request` response:
   ```
   Cannot perform the operation: The list of usernames is null or empty!
   ```
   
2. The service queries the database to find if there are users with
such usernames. The resulting list is then checked if it has the same 
   length as the provided one. If no, the service returns a `400 Bad Request`
   response. If yes, the service returns an `200 OK` response, containing the
   ids of the users in the response body.
   
   ###### Example Success: 
   ```
   [
    1,
    45,
    23,
    7
   ]
   ```

   ###### Example Failure:
   ```
   Number of user ids fetched is different
   from the number of provided usernames! 
   Either a names was duplicated or one of the usernames
   did not exist.
   ```
   
If the fetching of the users from the database threw any exception, the
service sends a `400 Bad Request` response, with the cause of
the exception in an `ERROR` header.

#### Resetting the microservice: `DELETE  '/reset'`
This endpoint is meant for administrators who want to clear the whole database of
the microservice. The endpoint does not make any checks as to the authority of the 
request sender. It just tries to clear the whole user database. If it succeeds it
sends a `200 OK` response to the user:
```
The clearing of the database was successful!
```

If the operation fails for some reason, the microservice sends a `500 Internal server error`
response to the user:
```
The clearing of the user database failed!
```


## Possible Future Upgrades to the Microservice

* Implement an endpoint for changing a user's password.

* Add a "Forgot password" possibility.

* Provide better security for the application. Currently, its security depends on
  the one provided by the API Gateway. In reality, if a user knows the address of the microservice,
  he can easily make requests to the service.

* Implement different user roles. Currently, there is only one type. This prevents 
the application from authorizing certain endpoints like for example the `/reset` one,
  and so anyone at any time can reset the application, which is very bad.
  
* Changing the endpoints so that there is more consistency as to how
error are handled and informed to the user.
  
