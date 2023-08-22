package nl.tudelft.sem.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import nl.tudelft.sem.model.User;
import nl.tudelft.sem.registration.CheckConflicts;
import nl.tudelft.sem.registration.RegisterUser;
import nl.tudelft.sem.registration.Registrator;
import nl.tudelft.sem.registration.ValidateCredentials;
import nl.tudelft.sem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserAuthenticationServiceTest {
    transient UserAuthenticationService userAuthenticationService;
    transient UserRepository userRepository;
    transient ResponseEntity<String> res;

    transient User user;
    transient String username;
    transient String password;
    transient String email;

    @BeforeEach
    void before() {
        userRepository = mock(UserRepository.class);
        userAuthenticationService = new UserAuthenticationService(userRepository);
        username = "itsmebad";
        password = "th!s1saGoodp@ss";
        email = "thisaemail@gmail.com";
        user = new User(username, email, password);
    }

    @Test
    void registrationInvalidTest() {
        String username2 = "itsme";
        User user2 = new User(username2, email, password);

        res = userAuthenticationService.registerNewUser(user2);
        System.out.println(res.getBody());
        assertTrue(res.getBody().contains("Bad credentials: "));
        assertTrue(res.getBody().contains("Username should be between 8 and 24 characters"));
        assertEquals(406, res.getStatusCodeValue());
    }

    @Test
    void registrationNoEmailTest() {
        String username = "itsmebad";
        String password = "th!s1saGoodp@ss";

        res = userAuthenticationService.registerNewUser(new User(username, password));

        assertTrue(res.getBody().contains("Bad credentials: "));
        assertTrue(res.getBody().contains("No email address entered"));
        assertEquals(406, res.getStatusCodeValue());
    }
}
