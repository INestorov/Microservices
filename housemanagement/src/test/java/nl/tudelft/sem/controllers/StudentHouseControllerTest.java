package nl.tudelft.sem.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.models.StudentHouse;
import nl.tudelft.sem.models.User;
import nl.tudelft.sem.repositories.StudentHouseRepository;
import nl.tudelft.sem.repositories.UserRepository;
import nl.tudelft.sem.services.HouseManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

class StudentHouseControllerTest {
    transient HouseManagementService houseManagementService;
    transient StudentHouseController studentHouseController;
    @Mock
    transient UserRepository userRepository;
    @Mock
    transient StudentHouseRepository studentHouseRepository;
    @Mock
    transient StudentHouse studentHouseMock;
    @Mock
    transient User mockUser;
    transient int mockId;

    transient StudentHouse studentHouse1;
    transient StudentHouse studentHouse2;
    transient int houseId;
    transient String houseName1;
    transient int userId1;
    transient int userId2;
    transient User user1;
    transient User user2;
    transient List<Integer> idList1 = new ArrayList<>();
    transient List<Integer> idList2 = new ArrayList<>();

    @BeforeEach
    void setup() {
        houseManagementService = mock(HouseManagementService.class);
        studentHouseController = new StudentHouseController(houseManagementService);

        userRepository = mock(UserRepository.class);
        studentHouseRepository = mock(StudentHouseRepository.class);
        when(houseManagementService.getUserRepository()).thenReturn(userRepository);
        when(houseManagementService.getStudentHouseRepository()).thenReturn(studentHouseRepository);

        //Mock Student House to mock auto-generated id
        mockId = 55;
        studentHouseMock = mock(StudentHouse.class);
        mockUser = mock(User.class);

        houseId = 8;
        houseName1 = "Huize Nachtegaal";
        studentHouse1 = new StudentHouse(houseName1);

        studentHouse2 = new StudentHouse("Huize Cinzano");

        userId1 = 1;
        user1 = new User(userId1, studentHouse1);
        userId2 = 2;
        user2 = new User(userId2, studentHouse2);

        //define Repository behaviour
        when(userRepository.findById(userId1)).thenReturn(java.util.Optional.ofNullable(user1));
        when(userRepository.findById(userId2)).thenReturn(java.util.Optional.ofNullable(user2));
        when(userRepository.findById(mockId)).thenReturn(java.util.Optional.ofNullable(mockUser));
        when(studentHouseRepository.findById(houseId))
                .thenReturn(java.util.Optional.ofNullable(studentHouse1));
        when(studentHouseRepository.findByName(houseName1)).thenReturn(studentHouse1);
        when(mockUser.getHouse()).thenReturn(studentHouseMock);
        when(studentHouseMock.getHouseId()).thenReturn(85);
    }

    @Test
    void registerUserSuccessTest() {
        assertEquals("User with id 3 was added successfully",
                studentHouseController.registerUser(3).getBody());
    }

    @Test
    void registerUserNullIdTest() {
        assertTrue(studentHouseController.registerUser(null)
                .getBody().contains("This did not work as expected"));
    }

    @Test
    void createStudentHouseSuccessTest() {
        assertTrue(studentHouseController.createStudentHouse("Kantoor")
                .getBody().contains("successfully"));
    }

    @Test
    void deleteHouseSuccessTest() {
        assertTrue(studentHouseController.deleteStudentHouse(8)
                .getBody().contains("successfully"));
    }

    @Test
    void deleteHouseNonExistentHouseTest() {
        assertTrue(studentHouseController.deleteStudentHouse(66)
                .getBody().contains("No studentHouse for this id"));
    }

    @Test
    void joinHouseSuccessTest() {
        assertTrue(studentHouseController.joinStudentHouse("Huize Nachtegaal", 2)
                .getBody().contains("successfully"));
    }

    @Test
    void joinHouseNameNonExistentTest() {
        assertTrue(studentHouseController.joinStudentHouse("Non-Existent", 2)
                .getBody().contains("student house does not exist"));
    }

    @Test
    void joinHouseUserNonExistentTest() {
        assertEquals("This user does not exist", studentHouseController
                .joinStudentHouse("Huize Nachtegaal", -1).getBody());
    }

    @Test
    void leaveHouseSuccessTest() {
        assertTrue(studentHouseController.leaveStudentHouse(2)
                .getBody().contains("successfully left the house"));
    }

    @Test
    void leaveHouseNoHouseTest() {
        when(mockUser.getHouse()).thenReturn(null);
        assertEquals("This student house does not exist",
                studentHouseController.leaveStudentHouse(55).getBody());
    }

    @Test
    void leaveHouseUserNonExistentTest() {
        assertEquals("This user does not exist",
                studentHouseController.leaveStudentHouse(-1).getBody());
    }

    @Test
    void getHouseIdNonExistentUserIdTest() {
        assertEquals(ResponseEntity.badRequest().build(),
                studentHouseController.getStudentHouseId(44));
    }

    @Test
    void getHouseIdSuccessfulTest() {
        assertEquals(ResponseEntity.ok(85 + ""),
                studentHouseController.getStudentHouseId(55));
    }

    @Test
    void resetDatabaseTest() {
        assertEquals("The clearing of the database was successful!",
                studentHouseController.resetDatabase().getBody());
    }

    @Test
    void validateHousematesEmptyListTest() {
        assertEquals(idList1, studentHouseController
                .validateHousemates(idList2, 2));
    }

    @Test
    void validateHousematesOneHousemateManyEatersTest() {
        idList1.add(1);
        idList2.add(1);
        idList2.add(2);
        idList2.add(3);
        idList2.add(4);
        assertEquals(idList1, studentHouseController.validateHousemates(idList2, 1));
    }

    @Test
    void validateHousematesAllHousematesTest() {
        user2.setHouse(studentHouse1);
        idList1.add(1);
        idList1.add(2);
        idList2.add(1);
        idList2.add(2);
        assertEquals(idList1, studentHouseController.validateHousemates(idList2, 1));
    }

    @Test
    void validateHousematesNonExistentHousemateTest() {
        idList1.add(1);
        idList2.add(-1);
        idList2.add(1);
        idList2.add(2);
        assertEquals(idList1, studentHouseController.validateHousemates(idList2, 1));
    }

    @Test
    void validateHousematesNonExistentUserTest() {
        idList1.add(-1);
        idList2.add(1);
        idList2.add(2);
        assertEquals(idList1, studentHouseController.validateHousemates(idList2, -1));
    }

    @Test
    void addHousemateSuccessTest() {
        assertTrue(studentHouseController.addHousemate(1, 2)
                .getBody().contains("was added to the house of user with id"));
    }

    @Test
    void addHousemateNonExistentResidentTest() {
        assertEquals("This resident does not exist", studentHouseController
                .addHousemate(-1, 2).getBody());
    }

    @Test
    void addHousemateNonExistentNewResidentTest() {
        assertEquals("This possible housemate does not exist", studentHouseController
                .addHousemate(1, -1).getBody());
    }

    @Test
    void getHousematesOneResidentTest() {
        assertEquals("[1]", studentHouseController
                .getHousemates(1));
    }

    @Test
    void getHousematesNonExistentUserTest() {
        assertEquals(null,
                studentHouseController.getHousemates(-1));
    }

    @Test
    void getHousematesHouseNonExistentTest() {
        when(mockUser.getHouse()).thenReturn(null);
        assertEquals(null, studentHouseController.getHousemates(55));
    }

    @Test
    void getHousematesMultipleHousematesTest() {
        user2.moveIn(studentHouse1);
        assertEquals("[1,2]", studentHouseController.getHousemates(2));
    }

}
