package nl.tudelft.sem.foodservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.foodservice.models.CreditAccount;
import nl.tudelft.sem.foodservice.models.FoodProduct;
import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.repositories.FoodProductRepository;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class SpoiledFoodTest {

    @Mock
    transient CreditsController creditsController;

    transient FoodProductController foodProductController;

    transient CreditAccount creditAccount;
    transient CreditAccount creditAccount2;
    transient CreditAccount creditAccount3;

    @Mock
    transient CreditRepository creditRepository;
    @Mock
    transient FoodProductRepository foodProductRepository;
    transient FoodManagementService foodManagementService;
    transient FoodProduct tomatoes;

    @BeforeEach
    void setUp() {
        creditAccount = new CreditAccount(1, 30);
        creditAccount2 = new CreditAccount(2, -55);
        creditAccount3 = new CreditAccount(3, 15);

        creditRepository = mock(CreditRepository.class);
        foodProductRepository = mock(FoodProductRepository.class);

        tomatoes = new FoodProduct(
                "tomatoes", 1.20f, LocalDate.of(2020, 12,
                6), 2,
                "a piece", 1, 1, 2);

        when(foodProductRepository.findById(3)).thenReturn(java.util.Optional.ofNullable(tomatoes));

        foodManagementService = new FoodManagementService(foodProductRepository, creditRepository);

        creditsController = mock(CreditsController.class);
        foodProductController = new FoodProductController(foodManagementService,
                creditsController);
    }

    @Test
    void spoiledFoodProductTest() {

        assertTrue(foodProductController.spoiledFoodProduct(2, 3, 1)
                .contains(" because it was spoiled."));
    }

    @Test
    void spoiledFoodProductTest_NonExsistentFoodId() {
        when(foodProductRepository.findById(4)).thenReturn(java.util.Optional.ofNullable(null));

        assertTrue(foodProductController.spoiledFoodProduct(2, 4, 1)
                .contains("this food product doesn't exist."));

    }


    @Test
    void getFoodInStorageSpoiledTest() {
        List<FoodProduct> storage2 = new ArrayList<>();
        storage2.add(tomatoes);
        when(foodProductRepository.findAllBystorageId(2)).thenReturn(
                java.util.Optional.ofNullable(storage2));

        when(foodProductRepository.findById(3))
                .thenReturn(java.util.Optional.ofNullable(tomatoes));
        tomatoes.setId(3);

        //storage including spoiled food
        assertTrue(foodProductController.getFoodInStorage(2, 1).getBody().contains(
                "because it was spoiled."));

        when(foodProductRepository.findAllBystorageId(2)).thenReturn(
                java.util.Optional.ofNullable(null));
        //storage after spoiled food was deleted
        assertEquals("This request did not work. The storage is empty.",
                foodProductController.getFoodInStorage(2, 1).getBody());
    }

    @Test
    void getAllUserIdsHouseTest() {
        int[] userIds = new int[]{1, 2, 3};
        when(creditsController.getAllUserIdsHouse(1)).thenReturn(userIds);
        int[] test = creditsController.getAllUserIdsHouse(1);
        assertEquals(userIds[0], test[0]);
        assertEquals(userIds[1], test[1]);
        assertEquals(userIds[2], test[2]);
        assertEquals(userIds.length, test.length);
    }
}
