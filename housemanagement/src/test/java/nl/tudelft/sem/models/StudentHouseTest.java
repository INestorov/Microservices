package nl.tudelft.sem.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentHouseTest {
    public transient StudentHouse house;
    public transient StudentHouse house2;
    public transient User user;
    public transient User user2;
    public transient List<User> residents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        house = new StudentHouse("Huize Nachtegaal");
        house2 = new StudentHouse("Grote blauwe huis");
        user = new User(8, house);
        user2 = new User(9, house2);

        house2.setResidents(residents);
        residents.add(user);
        house.setResidents(residents);
    }

    @Test
    void getName() {
        assertEquals("Huize Nachtegaal", house.getName());
    }

    @Test
    void setName() {
        house.setName("Huize N8egaal");
        assertEquals("Huize N8egaal", house.getName());
    }

    @Test
    void getHouseId() {
        assertEquals(0, house.getHouseId());
    }

    @Test
    void setHouseId() {
        house.setHouseId(2);
        assertEquals(2, house.getHouseId());
    }

    @Test
    void emptyConstructorTest() {
        StudentHouse sh = new StudentHouse();
        assertEquals(null, sh.getName());
    }

    @Test
    void getAndSetResidents() {
        house.setResidents(residents);
        assertEquals(residents, house.getResidents());
    }

    @Test
    void addResident() {
        User housemate = new User(1, house);
        house.setResidents(residents);
        house.addResident(housemate);
        residents.add(housemate);
        assertEquals(residents, house.getResidents());
    }

    @Test
    void removeResident() {
        assertTrue(house.getResidents().contains(user));
        house.removeResident(user);
        assertFalse(house.getResidents().contains(user));

        assertFalse(house.getResidents().contains(user2));
        house.removeResident(user2);
        assertFalse(house.getResidents().contains(user2));

        house2.removeResident(user2);
    }

    @Test
    void houseEquals() {
        assertEquals(house, house);
        assertNotEquals(house, house2);
        assertNotEquals(house, user);
        assertNotEquals(house, null);
    }
}