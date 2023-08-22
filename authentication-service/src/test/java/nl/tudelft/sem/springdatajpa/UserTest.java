package nl.tudelft.sem.springdatajpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.sem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    public transient User alice;
    public transient User bob;

    @BeforeEach
    void setUp() {
        alice = new User("aliceInWonderland", "alice@Wonderland.de", "Headmaker");
        bob = new User("bobTheBuilder", "bob@builds.nl", "JaWirSchaffenDas");
    }

    @Test
    void getUsernameTest() {
        assertEquals("aliceInWonderland", alice.getUsername());
        assertNotEquals("bobIsTheBuilder", bob.getUsername());
    }

    @Test
    void getEmailTest() {
        assertEquals("alice@Wonderland.de", alice.getEmail());
        assertNotEquals("bobTheBuilder@mail.com", bob.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals("Headmaker", alice.getPassword());
        assertNotEquals("NeinWirSchaffenDasNicht", bob.getPassword());
    }

    @Test
    void setUsernameTest() {
        bob.setUsername("bobIsTheBuilder");
        assertEquals("bobIsTheBuilder", bob.getUsername());
    }

    @Test
    void setEmailTest() {
        bob.setEmail("bobTheBuilder@mail.com");
        assertEquals("bobTheBuilder@mail.com", bob.getEmail());
    }

    @Test
    void setPassword() {
        bob.setPassword("NeinWirSchaffenDasNicht");
        assertEquals("NeinWirSchaffenDasNicht", bob.getPassword());
    }
}