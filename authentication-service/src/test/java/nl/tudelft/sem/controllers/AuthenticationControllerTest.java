package nl.tudelft.sem.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.DestroyFailedException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.services.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

public class AuthenticationControllerTest {

    @Mock
    transient UserAuthenticationService userAuthenticationService;

    transient AuthenticationController authenticationController;
    transient User user;
    transient String username;
    transient String password;
    transient String email;
    transient ResponseEntity<String> res;
    transient User user3;

    @BeforeEach
    void setup() {
        userAuthenticationService = mock(UserAuthenticationService.class);
        authenticationController = new AuthenticationController(userAuthenticationService);
        username = "itsmebad";
        password = "th!s1saGoodp@ss";
        email = "thisaemail@gmail.com";
        user = new User(username, email, password);
        user3 = new User(username, password);
    }

    @Test
    void loginNullTest() {
        User user2 = new User("itsme", "secureenough");
        when(userAuthenticationService.login(user2)).thenReturn(null);

        res = authenticationController.login(user2);

        assertEquals("Login failed", res.getBody());
        assertEquals(401, res.getStatusCodeValue());
    }

    @Test
    void loginSuccessTest() {
        when(userAuthenticationService.login(user3)).thenReturn(1);

        res = authenticationController.login(user3);

        assertEquals("1", res.getBody());
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void registrationNoEmailTest() throws Exception {
        String username = "itsmebad";
        String password = "th!s1saGoodpass";

        when(userAuthenticationService.registerNewUser(any()))
                .thenReturn(ResponseEntity.status(406).body(""));

        res = authenticationController.registerUser(new User(username, password));

        assertEquals(406, res.getStatusCodeValue());
    }

    @Test
    void registrationSuccessTest() throws Exception {
        when(userAuthenticationService.registerNewUser(any()))
                .thenReturn(ResponseEntity.status(201).body(1 + ""));

        res = authenticationController.registerUser(user);

        assertEquals("1", res.getBody());
        assertEquals(201, res.getStatusCodeValue());
    }

    @Test
    void unregisterNotExistingTest() {
        when(userAuthenticationService.unregisterUser(user3)).thenReturn(false);

        res = authenticationController.unregisterUser(user3);

        assertEquals("Bad credentials: Could not unregister user with the provided credentials!",
                res.getBody());
        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void registerSuccessTest() {
        when(userAuthenticationService.unregisterUser(user3)).thenReturn(true);

        res = authenticationController.unregisterUser(user3);

        assertEquals("User was successfully unregistered!", res.getBody());
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void idNullTest() {
        ResponseEntity<List<Integer>> res2 = authenticationController.getIdsOfUsers(null);
        ResponseEntity<List<Integer>> res3 = authenticationController
                .getIdsOfUsers(new ArrayList<String>());

        assertEquals(400, res2.getStatusCodeValue());
        assertEquals(400, res3.getStatusCodeValue());
    }

    @Test
    void idInvalidInputTest() throws InvalidInputException {
        List<String> list = Arrays.asList("Itsmetheuser");

        when(userAuthenticationService.getIdsForUsernames(list))
                .thenThrow(new InvalidInputException("The fetching of the users failed!"));

        ResponseEntity<List<Integer>> res2 = authenticationController.getIdsOfUsers(list);

        assertEquals(400, res2.getStatusCodeValue());
        assertEquals("[ERROR:\"The fetching of the users failed!\"]", res2.getHeaders().toString());
    }

    @Test
    void idSuccessTest() throws InvalidInputException {
        List<String> list = Arrays.asList("Itsmetheuser");

        when(userAuthenticationService.getIdsForUsernames(list)).thenReturn(Arrays.asList(1));

        ResponseEntity<List<Integer>> res2 = authenticationController.getIdsOfUsers(list);

        assertEquals(200, res2.getStatusCodeValue());
        assertEquals(Arrays.asList(1), res2.getBody());
    }

    @Test
    void resetDestroyTest() throws DestroyFailedException {
        doThrow(new DestroyFailedException("The clearing of the user database failed!"))
                .when(userAuthenticationService).clearDatabase();

        res = authenticationController.resetUserDatabase();

        assertEquals(500, res.getStatusCodeValue());
        assertEquals("The clearing of the user database failed!", res.getBody());
    }

    @Test
    void resetSuccessTest() throws DestroyFailedException {
        res = authenticationController.resetUserDatabase();

        assertEquals(200, res.getStatusCodeValue());
        assertEquals("The clearing of the database was successful!", res.getBody());
    }
}
