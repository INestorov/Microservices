package nl.tudelft.sem.foodservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.foodservice.models.CreditAccount;
import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;


public class CreditsControllerTest {
    transient FoodManagementService foodManagementService;
    transient CreditsController creditsController;
    @Mock
    transient CreditRepository creditRepository;

    transient CreditAccount creditAccount;
    transient CreditAccount creditAccount2;
    transient CreditAccount creditAccount3;

    @BeforeEach
    void setUp() {
        creditAccount = new CreditAccount(1, 30);
        creditAccount2 = new CreditAccount(2, -55);
        creditAccount3 = new CreditAccount(3, 15);

        creditRepository = mock(CreditRepository.class);

        foodManagementService = mock(FoodManagementService.class);
        when(foodManagementService.getCreditRepository()).thenReturn(creditRepository);


        creditsController = new CreditsController(foodManagementService);
    }

    @Test
    void getAllCreditAccountsTest() {
        //set-up account reactions
        List<CreditAccount> test = new ArrayList<>();
        test.add(creditAccount);
        test.add(creditAccount2);
        when(creditRepository.findAll()).thenReturn(test);

        //tests method
        assertEquals(creditsController.getAllCreditAccounts(), test);
    }

    @Test
    void getCreditByIdTest() {
        //set-up account reactions
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(creditAccount2));
        when(creditRepository.findCreditAccountByUserId(3))
                .thenReturn(java.util.Optional.ofNullable(null));

        //positive Account
        ResponseEntity<String> res = creditsController.getCreditById(1);
        assertEquals("You have 30.0 credits. \n", res.getBody());

        //negative Account
        res = creditsController.getCreditById(2);
        assertEquals("You have -55.0 credits. \nYour Credits are insufficient. "
                + "Please go grocery shopping as soon as possible", res.getBody());

        //non-existing Account
        res = creditsController.getCreditById(3);
        assertEquals("There exists no user with this userId.", res.getBody());
    }

    @Test
    void addCreditsTest() {
        ResponseEntity<String> output = creditsController.addCredits(3);
        assertEquals("successfully added Credit Account. "
                + "Your current balance is 0.0", output.getBody());
    }

    @Test
    void updateCreditsTest() {
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(creditAccount2));
        when(creditRepository.findCreditAccountByUserId(3))
                .thenReturn(java.util.Optional.ofNullable(null));

        ResponseEntity<String> output = creditsController.updateCredits(1, 20);
        assertEquals("The credits have been updated. The new balance is 50.0. \n",
                output.getBody());

        output = creditsController.updateCredits(2, -20);
        assertTrue(output.getBody().contains(
                "The credits have been updated. The new balance is -75.0. "
                        + "\nYour Credits are insufficient."));

        output = creditsController.updateCredits(3, 20);
        assertEquals("This credit account does not exist.", output.getBody());
    }

    @Test
    void resetCreditsTest() {
        List<CreditAccount> test = new ArrayList<>();
        test.add(creditAccount);
        test.add(creditAccount2);
        when(creditRepository.findAll()).thenReturn(test);

        ResponseEntity<String> res = creditsController.resetCredits();

        assertEquals("All credit accounts have been reset to 0", res.getBody());
        assertEquals(0.0, creditAccount.getAmount());
        assertEquals(0.0, creditAccount2.getAmount());


    }

    @Test
    void removeCreditAccountTest() {
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(3))
                .thenReturn(java.util.Optional.ofNullable(null));

        assertEquals("The Account has been successfully deleted.", creditsController
                .removeCreditAccount(1).getBody());

        verify(creditRepository, times(1)).deleteCreditAccountByUserId(1);

        assertEquals("There is no Account with that userId", creditsController
                .removeCreditAccount(3).getBody());
    }

    @Test
    void insufficentCreditsTest() {
        String res = creditsController.insufficientCredits(creditAccount.getAmount());
        assertEquals("", res);
        res = creditsController.insufficientCredits(creditAccount2.getAmount());
        assertEquals("Your Credits are insufficient. "
                + "Please go grocery shopping as soon as possible", res);
        res = creditsController.insufficientCredits(-49);
        assertEquals("", res);
        res = creditsController.insufficientCredits(-50);
        assertEquals("", res);
        res = creditsController.insufficientCredits(-51);
        assertEquals("Your Credits are insufficient. "
                + "Please go grocery shopping as soon as possible", res);
    }

    @Test
    void getCreditAccountTest() {
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(creditAccount2));
        when(creditRepository.findCreditAccountByUserId(3))
                .thenReturn(java.util.Optional.ofNullable(null));

        assertEquals(creditAccount, creditsController.getCreditAccount(1));
        assertEquals(creditAccount2, creditsController.getCreditAccount(2));
        assertEquals(null, creditsController.getCreditAccount(3));
    }

    @Test
    void splitCreditsSomeHousematesTest() {
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(creditAccount2));

        int[] userIds = {1, 2};

        creditsController.splitCreditsSomeHousemates(-30, userIds);

        assertEquals(15, creditAccount.getAmount());
        assertEquals(-70, creditAccount2.getAmount());
    }

    @Test
    void splitCreditsAllHousematesTest() {
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(creditAccount));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(creditAccount2));
        when(creditRepository.findCreditAccountByUserId(3)).thenReturn(
                java.util.Optional.ofNullable(creditAccount3));

        int[] userIds = new int[]{1, 2, 3};
        CreditsController creditsControllerSpy = Mockito.spy(creditsController);
        Mockito.doReturn(userIds).when(creditsControllerSpy).getAllUserIdsHouse(1);
        creditsController.splitCreditsSomeHousemates(-45, userIds);
        assertEquals(15, creditAccount.getAmount());
        assertEquals(-70, creditAccount2.getAmount());
        assertEquals(0, creditAccount3.getAmount());
    }

    @Test
    void splitCreditsAllHousematesTest2() {
        int[] userIds = new int[]{1, 2, 3};
        CreditsController creditsControllerSpy = Mockito.spy(creditsController);
        Mockito.doReturn(userIds).when(creditsControllerSpy).getAllUserIdsHouse(1);
        creditsControllerSpy.splitCreditsAllHousemates(20, 1);
        verify(creditsControllerSpy, times(1)).splitCreditsSomeHousemates(20, userIds);
    }

    @Test
    void getIdsConnectionTest() {

        HttpURLConnection httpurlconnectionspy = mock(HttpURLConnection.class);
        creditsController.setUpConnection(httpurlconnectionspy);
        verify(httpurlconnectionspy, times(1)).setUseCaches(false);
        verify(httpurlconnectionspy, times(1)).setAllowUserInteraction(false);
        verify(httpurlconnectionspy, times(1)).setConnectTimeout(5000);
        verify(httpurlconnectionspy, times(1)).setReadTimeout(5000);

    }

}
