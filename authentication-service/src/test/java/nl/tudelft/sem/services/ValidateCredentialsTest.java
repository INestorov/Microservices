package nl.tudelft.sem.services;

import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.registration.ValidateCredentials;
import org.junit.jupiter.api.Test;

public class ValidateCredentialsTest {

    @Test
    void emailTestInvalid() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyEmail("imnotanemail"));
    }

    @Test
    void emailNullTest() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyEmail(null));
    }

    @Test
    void emailTestValid() throws InvalidInputException {
        ValidateCredentials.verifyEmail("imnotanemail@gmail.com");
    }

    @Test
    void passwordTestInvalid() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyPassword("Id0ntcontainspecials"));
    }

    @Test
    void passwordNullTest() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyPassword(null));
    }

    @Test
    void passwordTestValid() throws InvalidInputException {
        ValidateCredentials.verifyPassword("I@ms0secure");
    }

    @Test
    void usernameTestInvalid() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyUsername("imsoloooooooooooooooooong"));
    }

    @Test
    void usernameNullTest() {
        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidInputException.class,
                () -> ValidateCredentials.verifyUsername(null));
    }

    @Test
    void usernameTestValid() throws InvalidInputException {
        ValidateCredentials.verifyUsername("itsmyusername");
    }
}
