package nl.tudelft.sem.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {
    public transient User user;
    public transient User user2;
    public transient User user3;
    public transient StudentHouse houseNachtegaal;
    public transient StudentHouse houseMol;

    @BeforeEach
    void setUp() {
        houseNachtegaal = new StudentHouse("Huize Nachtegaal");
        houseMol = new StudentHouse("Huize Mol");
        user = new User(8, houseNachtegaal);
        user2 = new User(9, houseMol);
        user3 = new User(4, houseNachtegaal);
    }

    @Test
    void getHouse() {
        assertEquals(houseNachtegaal, user.getHouse());
    }

    @Test
    void moveInTest() {
        assertEquals(houseNachtegaal, user.getHouse());
        List<User> userList = new ArrayList<>();
        userList.add(user2);
        user.moveIn(houseMol);
        userList.add(user);
        assertEquals(houseMol, user.getHouse());
        assertEquals(userList, houseMol.getResidents());
    }

    @Test
    void setUserId() {
        user.setUserId(1);
        assertEquals(1, user.getUserId());
    }

    @Test
    void moveOutTest() {
        assertEquals(houseNachtegaal, user.getHouse());
        user.moveOut();
        assertEquals(null, user.getHouse());
        assertEquals(1, houseNachtegaal.getResidents().size());
    }

    @Test
    void changeHouse() {
        user.setHouse(houseMol);
        user.moveOut();
        user.setHouse(houseNachtegaal);
        assertEquals(houseNachtegaal, user.getHouse());
    }

    @Test
    void userEqualsTest() {
        assertEquals(user, user);
        assertEquals(user, new User(8, houseNachtegaal));
        assertNotEquals(user, user2);
        assertNotEquals(user, houseMol);
        assertNotEquals(user, user3);
        assertNotEquals(user, Integer.valueOf(3));
        User user4 = new User(5, houseNachtegaal);

        assertNotEquals(user, user4);

        User user5 = new User(8, houseMol);
        assertNotEquals(user, user5);
    }

    @Test
    void addHousemateTest() {
        User user4 = new User(1, houseMol);

        user.addHousemate(user4);
        assertEquals(3, houseNachtegaal.getResidents().size());
        assertEquals(1, houseMol.getResidents().size());
        assertEquals(user.getHouse(), user4.getHouse());
    }

    @Test
    void hashCodeTest() {
        assertEquals(Objects.hash(user.getUserId(), user.getHouse()), user.hashCode());
    }
}