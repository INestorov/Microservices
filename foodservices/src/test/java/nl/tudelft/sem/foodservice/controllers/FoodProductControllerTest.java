package nl.tudelft.sem.foodservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.foodservice.models.CreditAccount;
import nl.tudelft.sem.foodservice.models.FoodProduct;
import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.repositories.FoodProductRepository;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

public class FoodProductControllerTest {
    public static final String OREOS = "oreos";
    public static final String PIECES = "pieces";
    public static final String SUCCESSFULLY_TAKEN = "successfully taken";
    transient FoodManagementService foodManagementService;
    transient FoodProductController foodProductController;
    transient CreditsController creditsController;
    @Mock
    transient FoodProductRepository foodProductRepository;
    @Mock
    transient CreditRepository creditRepository;

    transient FoodProduct cookies;
    transient FoodProduct sauce;
    transient FoodProduct tomatoes;

    transient CreditAccount user1;
    transient CreditAccount user2;
    transient CreditAccount user3;

    transient int[] userIds;

    @BeforeEach
    void setUp() {
        cookies = new FoodProduct(
                OREOS, 2.99f, LocalDate.now().plusMonths(3), 21,
                PIECES, 3, 1, 1);
        sauce = new FoodProduct(
                "hamburger sauce", 1.80f,
                LocalDate.now().plusYears(1),
                750, "ml",
                10, 2, 1);
        tomatoes = new FoodProduct(
                "tomatoes", 1.20f, LocalDate.now().minusDays(7), 2,
                PIECES, 1, 1, 2);

        user1 = new CreditAccount(1, 50);
        user2 = new CreditAccount(2, 0);
        user3 = new CreditAccount(3, 25);
        userIds = new int[]{1, 2, 3};
        foodProductRepository = mock(FoodProductRepository.class);

        creditRepository = mock(CreditRepository.class);

        foodManagementService = mock(FoodManagementService.class);
        when(foodManagementService.getCreditRepository()).thenReturn(creditRepository);
        creditsController = new CreditsController(foodManagementService);
        when(foodManagementService.getFoodProductRepository()).thenReturn(foodProductRepository);
        foodProductController = new FoodProductController(foodManagementService, creditsController);

        //sorts foodItems into storages
        List<FoodProduct> storage1 = new ArrayList<>();
        storage1.add(cookies);
        storage1.add(sauce);
        List<FoodProduct> storage2 = new ArrayList<>();
        storage2.add(tomatoes);
        //defines behaviour of the Repository when including the appropriate storageIds
        when(foodProductRepository.findAllBystorageId(1)).thenReturn(
                java.util.Optional.ofNullable(storage1));
        when(foodProductRepository.findAllBystorageId(2)).thenReturn(
                java.util.Optional.ofNullable(storage2));
        when(foodProductRepository.findAllBystorageId(3)).thenReturn(
                java.util.Optional.ofNullable(null));
    }

    @Test
    void getFoodinStorageTest() {
        //default storage case
        assertEquals("[" + cookies.toString() + ", " + sauce.toString() + "]",
                foodProductController.getFoodInStorage(1, 1).getBody());

        //storage that was empty in the first place
        assertEquals("This request did not work. The storage is empty.",
                foodProductController.getFoodInStorage(3, 1).getBody());
    }


    @Test
    void addFoodProductTest() {
        FoodProduct soda = new FoodProduct("Cola", 0.50f,
                LocalDate.now().plusMonths(6), 1500, "ml",
                500, 1, 2);


        assertEquals(Optional.empty(), creditRepository.findCreditAccountByUserId(2));

        ResponseEntity<String> res = foodProductController.addFoodProduct(2,
                soda, 1);
        assertNotEquals(null, creditRepository.findCreditAccountByUserId(2));

        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        assertEquals(soda.toString() + " created successfully", res.getBody());
    }

    @Test
    void getAllFoodProductsTest() {
        when(foodProductRepository.findAll()).thenReturn(null);
        assertEquals("failed to return all food products.", foodProductController
                .getAllFoodProducts().getBody());

        List<FoodProduct> test = new ArrayList<>();
        test.add(cookies);
        test.add(sauce);
        test.add(tomatoes);

        when(foodProductRepository.findAll()).thenReturn(test);
        assertEquals("The foodproducts are: [" + cookies.toString() + ", "
                + sauce.toString() + ", " + tomatoes.toString() + "]", foodProductController
                .getAllFoodProducts().getBody());
    }

    @Test
    void takePortionTest_NonExistFoodId() {
        when(foodProductRepository.findById(5)).thenReturn(java.util.Optional.ofNullable(tomatoes));
        ResponseEntity<String> res = foodProductController
                .takePortion(1, 5, 2, 1);
        assertEquals("No food product with this ID exists.", res.getBody());
    }

    @Test
    void takePortionTest_WrongStorageId() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        ResponseEntity<String> res = foodProductController
                .takePortion(2, 1, 2, 1);
        assertEquals("No food product with this ID exists.", res.getBody());
    }

    @Test
    void takePortionTest_takeAway() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        ResponseEntity<String> res = foodProductController
                .takePortion(1, 1, 3, 1);
        assertEquals(41.03, user1.getAmount(), 0.001);
        assertTrue(res.getBody().contains(SUCCESSFULLY_TAKEN));
        Optional<FoodProduct> a = foodProductRepository.findById(1);
        assertEquals(12, a.get().getQuantity());
    }

    @Test
    void takePortionTest_putBack() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        ResponseEntity<String> res = foodProductController
                .takePortion(1, 1, -4, 1);
        assertEquals(61.96, user1.getAmount(), 0.001);
        assertTrue(res.getBody().contains("successfully put back"));
        Optional<FoodProduct> a = foodProductRepository.findById(1);
        assertEquals(33, a.get().getQuantity());
    }

    @Test
    void takePortionTest_TakeMoreThanAvailable() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        ResponseEntity<String> res = foodProductController
                .takePortion(1, 1, 22, 1);
        assertTrue(res.getBody().contains("There is not enough"));
    }

    @Test
    void takePortionTest_NewQuantityZero() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        ResponseEntity<String> res = foodProductController
                .takePortion(1, 1, 7, 1);
        assertEquals(29.07, user1.getAmount(), 0.001);
        assertTrue(res.getBody().contains(SUCCESSFULLY_TAKEN));
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(null));
        Optional<FoodProduct> a = foodProductRepository.findById(1);
        assertFalse(a.isPresent());
    }

    @Test
    void deleteFoodProductTest() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        ResponseEntity<String> res = foodProductController.deleteFoodProduct(1, 1);
        assertTrue(res.getBody().contains("successfully deleted."));
    }

    @Test
    void resetTest() {
        assertEquals("successfully deleted all foodProducts.",
                foodProductController.resetFoodProducts().getBody());
    }

    @Test
    void resetStorageTest() {
        assertEquals("successfully deleted all foodProducts in storage 1",
                foodProductController.resetStorage(1).getBody());
        assertEquals("successfully deleted all foodProducts in storage 2",
                foodProductController.resetStorage(2).getBody());
        assertEquals("No storage with this ID exists.",
                foodProductController.resetStorage(3).getBody());
    }

    @Test
    void updateFoodProduct_UserAddedFoodProduct_QuantityNotZero() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        FoodProduct updatedCookies = new FoodProduct(OREOS, 0.20f,
                LocalDate.now().plusYears(1), 15, "a piece",
                3, 1, 1);
        assertEquals(50, user1.getAmount(), 0.001);
        ResponseEntity<String> res = foodProductController
                .updateFoodProduct(1, 1, 1, updatedCookies);
        assertTrue(res.getBody().contains("foodproduct has been updated"));
        Optional<FoodProduct> cookies = foodProductRepository.findById(1);
        assertEquals(0.20, cookies.get().getPricePerPortion(), 0.001);
        assertEquals(30.07, user1.getAmount(), 0.001);
        assertEquals(LocalDate.now().plusYears(1), cookies.get()
                .getExpirationDate());
        assertEquals(OREOS, cookies.get().getName());


    }

    @Test
    void updateFoodProduct_UserAddedFoodProduct_QuantityZero() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        FoodProduct updatedCookies = new FoodProduct(OREOS, 0.20f,
                LocalDate.now().plusYears(1), 0, "a piece",
                3, 1, 1);
        ResponseEntity<String> res = foodProductController.updateFoodProduct(1, 1,
                1, updatedCookies);

        assertTrue(res.getBody().contains("The given foodProduct has been deleted"));
    }

    @Test
    void updateFoodProduct_UserDidNotAddProduct() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        FoodProduct updatedCookies = new FoodProduct(OREOS, 0.20f,
                LocalDate.now().plusYears(1), 15, "a piece",
                3, 1, 1);
        ResponseEntity<String> res = foodProductController.updateFoodProduct(1, 2,
                1, updatedCookies);

        assertTrue(res.getBody().contains("You did not create this food product, "
                + "so you may not edit it"));
        Optional<FoodProduct> cookies = foodProductRepository.findById(1);
        assertNotEquals(0.20, cookies.get().getPricePerPortion(), 0.001);
        assertNotEquals(LocalDate.now().plusYears(1), cookies.get()
                .getExpirationDate());

    }

    @Test
    void updateFoodProduct_NonExistFoodId() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(null));
        FoodProduct updatedCookies = new FoodProduct(OREOS, 0.20f,
                LocalDate.now().plusYears(1), 15, PIECES, 3,
                1, 1);
        ResponseEntity<String> res = foodProductController.updateFoodProduct(1,
                1, 1, updatedCookies);

        assertTrue(res.getBody().contains("This food product doesn't exist."));
    }

    @Test
    void updateFood_IllegalChanges() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        FoodProduct updatedCookies = new FoodProduct(OREOS, 0.20f,
                LocalDate.now().plusYears(1), 15, PIECES, 3,
                1, 2);
        ResponseEntity<String> res = foodProductController.updateFoodProduct(1,
                1, 1, updatedCookies);

        assertTrue(res.getBody().contains("foodproduct has been updated"));
        Optional<FoodProduct> cookies = foodProductRepository.findById(1);
        assertEquals(0.20, cookies.get().getPricePerPortion(), 0.001);
        assertEquals(LocalDate.now().plusYears(1), cookies.get()
                .getExpirationDate());
        assertEquals(OREOS, cookies.get().getName());

        assertNotEquals(2, cookies.get().getStorageId());


    }

    @Test
    void takePortionTogetherTest_NonExistentFoodId() {
        when(foodProductRepository.findById(5)).thenReturn(java.util.Optional.ofNullable(null));
        ResponseEntity<String> res = foodProductController
                .takePortionTogether(1, 5, 2, userIds);
        assertEquals("No food product with this ID exists.", res.getBody());
    }

    @Test
    void takePortionTogetherTest_TakeWith3() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(user2));
        when(creditRepository.findCreditAccountByUserId(3)).thenReturn(
                java.util.Optional.ofNullable(user3));
        ResponseEntity<String> res = foodProductController
                .takePortionTogether(1, 1, 5, userIds);
        assertTrue(res.getBody().contains(SUCCESSFULLY_TAKEN));
        assertEquals(45.0166, user1.getAmount(), 0.001);
        assertEquals(-4.983, user2.getAmount(), 0.001);
        assertEquals(20.0166, user3.getAmount(), 0.001);

        Optional<FoodProduct> a = foodProductRepository.findById(1);
        assertEquals(6, a.get().getQuantity());
    }

    @Test
    void takePortionTogetherTest_NotEnoughPortions() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        ResponseEntity<String> res = foodProductController
                .takePortionTogether(1, 1, 22, userIds);
        assertTrue(res.getBody().contains("There is not enough"));
    }

    @Test
    void takePortionTogetherTest_NewQuanitityZero() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        when(creditRepository.findCreditAccountByUserId(2)).thenReturn(
                java.util.Optional.ofNullable(user2));
        when(creditRepository.findCreditAccountByUserId(3)).thenReturn(
                java.util.Optional.ofNullable(user3));
        ResponseEntity<String> res = foodProductController
                .takePortionTogether(1, 1, 7, userIds);
        assertTrue(res.getBody().contains(SUCCESSFULLY_TAKEN));
        assertEquals(43.02333, user1.getAmount(), 0.001);
        assertEquals(-6.9766, user2.getAmount(), 0.001);
        assertEquals(18.02333, user3.getAmount(), 0.001);

    }

    @Test
    void takePortionTogetherTest_TakeAlone() {
        when(foodProductRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(cookies));
        when(creditRepository.findCreditAccountByUserId(1)).thenReturn(
                java.util.Optional.ofNullable(user1));
        int[] oneUser = new int[]{1};
        ResponseEntity<String> res = foodProductController
                .takePortionTogether(1, 1, 3, oneUser);
        assertEquals(41.03, user1.getAmount(), 0.001);
        assertTrue(res.getBody().contains(SUCCESSFULLY_TAKEN));
        Optional<FoodProduct> a = foodProductRepository.findById(1);
        assertEquals(12, a.get().getQuantity());
    }

}
