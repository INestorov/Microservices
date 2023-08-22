package nl.tudelft.sem.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import nl.tudelft.sem.models.StudentHouse;
import nl.tudelft.sem.models.User;
import nl.tudelft.sem.repositories.StudentHouseRepository;
import nl.tudelft.sem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


public class HouseManagementServiceTest {
    public transient HouseManagementService houseManagementService;
    @Mock
    public transient StudentHouseRepository studentHouseRepository;
    @Mock
    public transient UserRepository userRepository;
    public transient StudentHouse studentHouseNachtegaal;
    public transient StudentHouse studentHouseMol;
    public transient User residentNachtegaal;
    public transient User residentMol;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        studentHouseRepository = mock(StudentHouseRepository.class);
        houseManagementService = new HouseManagementService(studentHouseRepository, userRepository);
        studentHouseNachtegaal = new StudentHouse("Huize Nachtegaal");
        studentHouseMol = new StudentHouse("Molstraat");
        residentNachtegaal = new User(88, studentHouseNachtegaal);
        residentMol = new User(99, studentHouseMol);
    }

    @Test
    void getStudentHouseRepositoryTest() {
        assertEquals(studentHouseRepository, houseManagementService.getStudentHouseRepository());
    }

    @Test
    void getUserRepositoryTest() {
        assertEquals(userRepository, houseManagementService.getUserRepository());
    }

    @Test
    void addHousemateTest() {
        assertEquals(residentNachtegaal.getHouse(), studentHouseNachtegaal);
        assertNotEquals(residentMol.getHouse(), studentHouseNachtegaal);
        houseManagementService.addHousemate(residentNachtegaal, residentMol);
        assertEquals(residentMol.getHouse(), studentHouseNachtegaal);
    }

    @Test
    void joinStudentHouseTest() {
        assertNotEquals(residentNachtegaal.getHouse(), studentHouseMol);
        houseManagementService.joinStudentHouse(studentHouseMol, residentNachtegaal);
        assertEquals(residentNachtegaal.getHouse(), studentHouseMol);
    }

}
