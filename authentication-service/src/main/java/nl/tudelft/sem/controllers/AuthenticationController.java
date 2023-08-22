package nl.tudelft.sem.controllers;

import java.util.List;
import javax.security.auth.DestroyFailedException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.registration.CheckConflicts;
import nl.tudelft.sem.registration.RegisterUser;
import nl.tudelft.sem.registration.Registrator;
import nl.tudelft.sem.registration.ValidateCredentials;
import nl.tudelft.sem.services.UserAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/application/authentication")
public class AuthenticationController {
    static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    final transient UserAuthenticationService userAuthenticationService;

    public AuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    /**
     * Handles logging a user in.
     *
     * @param user the user to be logged in
     * @return a {@link ResponseEntity} indicating whether the operation was successful
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        logger.info(": Received a POST-request for login.");

        Integer id = userAuthenticationService.login(user);
        if (id == null) {
            return ResponseEntity.status(401).body("Login failed");
        }
        return ResponseEntity.ok(id + "");
    }

    /**
     * Receive a POST request for registering a new user in the database.
     *
     * @param user - The {@link User} object encapsulating
     *                            the credential information
     * @return A {@link ResponseEntity} indicating whether the operation was successful
     */
    @PostMapping("/sign-up")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        logger.info(": Received a POST-request for registration.");
        logger.info("Credentials: username: " + user.getUsername() + " ,email: " + user.getEmail()
                + ", password: " + user.getPassword());

        return userAuthenticationService.registerNewUser(user);
    }

    /** Receives a DELETE request for unregistering a user. It hands the credentials
     *      in to the service bean to check if the user exists and if the passwords
     *      match.
     *
     * @param user contains the user to unregister.
     * @return  A {@link ResponseEntity} indicating if the operation was successful.
     */
    @DeleteMapping("/unregister")
    public ResponseEntity<String> unregisterUser(@RequestBody User user) {
        logger.info(": Received a DELETE-request for unregister.");

        if (user.getUsername() == null || user.getPassword() == null) {
            logger.error("Cannot DELETE: Found null credential. Returning error response...");
            return ResponseEntity.status(400).body("Bad credentials: One of the unregister "
                    + "credentials was null.");
        }

        if (!userAuthenticationService.unregisterUser(user)) {
            return ResponseEntity.status(400).body("Bad credentials: Could not unregister user "
                    + "with the provided credentials!");
        }

        return ResponseEntity.ok().body("User was successfully unregistered!");
    }


    /** Receives a POST request for fetching the user ids of a list of users.
     *      The method expects the request to contain in the body a list of
     *      usernames for which to fetch the ids. It uses a bean of type
     *      {@link UserAuthenticationService} to fetch the usernames
     *      from the databases. If there is an error during the process
     *      the method returns an HTTP response Bad Request. Otherwise
     *      it returns the list of user ids in the body of the response.
     *
     * @param usernames The list of usernames for witch to fetch the ids.
     * @return A {@link ResponseEntity} indicating if the operation was successful,
     *      containing the list of user ids if it was.
     */
    @PostMapping("/user/get_names")
    public ResponseEntity<List<Integer>> getIdsOfUsers(@RequestBody List<String> usernames) {
        logger.info("Received POST request for the ids of a group of users");

        if (usernames == null || usernames.isEmpty()) {
            logger.error("Cannot perform operation: The list of usernames is null or empty!");
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(userAuthenticationService.getIdsForUsernames(usernames));
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().header("ERROR", e.getLocalizedMessage()).build();
        }

    }

    /** Receives a DELETE request for clearing the whole database.
     *
     * @return  A {@link ResponseEntity} indicating if the operation was successful.
     */
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetUserDatabase() {
        logger.info("Received a DELETE request for clearing the user database.");

        try {
            userAuthenticationService.clearDatabase();
            return ResponseEntity.ok().body("The clearing of the database was successful!");
        } catch (DestroyFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getLocalizedMessage());
        }
    }
}
