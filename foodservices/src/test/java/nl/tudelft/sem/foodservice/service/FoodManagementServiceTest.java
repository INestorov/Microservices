package nl.tudelft.sem.foodservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.foodservice.controllers.CreditsController;
import nl.tudelft.sem.foodservice.controllers.FoodProductController;
import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.repositories.FoodProductRepository;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FoodManagementServiceTest {
    public transient FoodManagementService foodManagementService;
    public transient FoodProductRepository food;
    public transient CreditRepository credits;

    @BeforeEach
    void setUp() {
        foodManagementService = new FoodManagementService(food, credits);
    }

    @Test
    void getFoodProductRepositoryTest() {
        assertEquals(food, foodManagementService.getFoodProductRepository());
    }

    @Test
    void getCreditRepositoryTest() {
        assertEquals(credits, foodManagementService.getCreditRepository());
    }
}
