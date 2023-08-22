package nl.tudelft.sem.foodservice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FoodProductTest {
    public transient FoodProduct cookies;
    public transient FoodProduct sauce;
    public transient FoodProduct tomatoes;


    @BeforeEach
    void setUp() {
        cookies = new FoodProduct(
                "oreos",
                2.95f,
                LocalDate.of(2020, 12, 31),
                21,
                "none",
                1, 1, 1);
        sauce = new FoodProduct(
                "hamburger sauce",
                4.75f,
                LocalDate.of(2022, 4, 1),
                1000,
                "ml",
                20, 1, 1);
        tomatoes = new FoodProduct(
                "tomatoes",
                1.20f,
                LocalDate.of(2020, 12, 5),
                2,
                "portion",
                1, 2, 1);
    }

    @Test
    void getIdTest() {
        assertEquals(0, cookies.getId());
    }

    @Test
    void setItTest() {
        cookies.setId(4);
        assertEquals(4, cookies.getId());
    }

    @Test
    void getNameTest() {
        assertEquals("tomatoes", tomatoes.getName());
    }

    @Test
    void setNameTest() {
        cookies.setName("kit kat");
        assertEquals("kit kat", cookies.getName());
    }

    @Test
    void getPriceTest() {
        assertEquals(2.95f, cookies.getPricePerPortion());
    }

    @Test
    void setPriceTest() {
        cookies.setPricePerPortion(1.99f);
        assertEquals(1.99f, cookies.getPricePerPortion());
    }

    @Test
    void getExpirationDateTest() {
        assertEquals("2020-12-31", cookies.getExpirationDate().toString());
    }

    @Test
    void setExpirationDateTest() {
        cookies.setExpirationDate(LocalDate.of(2020, 11, 30));
        assertEquals("2020-11-30", cookies.getExpirationDate().toString());
    }

    @Test
    void getQuantityTest() {
        assertEquals(21, cookies.getQuantity());
    }

    @Test
    void setQuantityTest() {
        cookies.setQuantity(23);
        assertEquals(23, cookies.getQuantity());
    }

    @Test
    void getMeasurementsTest() {
        assertEquals("ml", sauce.getMeasurements());
    }

    @Test
    void setMeasurementsTest() {
        cookies.setMeasurements("individual");
        assertEquals("individual", cookies.getMeasurements());
    }

    @Test
    void getPortionSizeTest() {
        assertEquals(1, cookies.getPortionSize());
    }

    @Test
    void setPortionSizeTest() {
        cookies.setPortionSize(3);
        assertEquals(3, cookies.getPortionSize());
    }

    @Test
    void getStorageId() {
        assertEquals(1, cookies.getStorageId());
    }

    @Test
    void setStorgageId() {
        cookies.setStorageId(2);
        assertEquals(2, cookies.getStorageId());
    }

    @Test
    void getAddedBy() {
        assertEquals(1, cookies.getAddedBy());
    }

    @Test
    void setAddedBy() {
        cookies.setAddedBy(2);
        assertEquals(2, cookies.getAddedBy());
    }

    @Test
    void testEquals() {
        assertEquals(cookies, cookies);
        assertNotEquals(sauce, cookies);
        sauce.setName("cookie");
        assertNotEquals(sauce, cookies);
        FoodProduct oreo = new FoodProduct(
                "oreos",
                2.95f,
                LocalDate.of(2020, 12, 31),
                21,
                "none",
                1, 1, 1);
        assertEquals(oreo, cookies);
    }

    @Test
    void testToString() {
        String oreosString = "FoodProduct{"
                + "foodId=" + 0
                + ", name='" + "oreos" + '\''
                + ", pricePerPortion=" + 2.95f
                + ", expirationDate='" + LocalDate.of(2020, 12, 31) + '\''
                + ", quantity=" + 21.0
                + ", measurements='" + "none" + '\''
                + ", portionSize=" + 1.0
                + ", addedBy=" + 1
                + ", storageId=" + 1
                + "}\n";

        assertEquals(oreosString, cookies.toString());
    }

    @Test
    void isSpoiledTest() {
        assertTrue(tomatoes.isSpoiled());
        assertFalse(sauce.isSpoiled());
    }

    @Test
    void testEqualsDifferentObject() {
        List<String> o = new ArrayList<>();
        assertNotEquals(cookies, o);
    }
}