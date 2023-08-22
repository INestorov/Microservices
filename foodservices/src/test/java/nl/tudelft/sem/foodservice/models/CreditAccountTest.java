package nl.tudelft.sem.foodservice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreditAccountTest {
    public transient CreditAccount creditAccount;
    public transient CreditAccount creditAccountNegativ;

    @BeforeEach
    void setUp() {
        creditAccount = new CreditAccount(1, 24);
        creditAccountNegativ = new CreditAccount(2, -64);
    }

    @Test
    void getUserIdTest() {
        assertEquals(1, creditAccount.getUserId());
        assertEquals(2, creditAccountNegativ.getUserId());
    }

    @Test
    void setUserIdTest() {
        assertEquals(1, creditAccount.getUserId());
        creditAccount.setUserId(3);
        assertEquals(3, creditAccount.getUserId());
    }

    @Test
    void getAmountTest() {
        assertEquals(24, creditAccount.getAmount());
        assertEquals(-64, creditAccountNegativ.getAmount());
    }

    @Test
    void setAmountTest() {
        assertEquals(24, creditAccount.getAmount());
        creditAccount.setAmount(365);
        assertEquals(365, creditAccount.getAmount());
    }

    @Test
    void constructorTest() {
        CreditAccount testAccount = new CreditAccount(3, -30);
        assertEquals(3, testAccount.getUserId());
        assertEquals(-30, testAccount.getAmount());
    }

}
