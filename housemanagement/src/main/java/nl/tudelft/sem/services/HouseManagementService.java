package nl.tudelft.sem.services;

import nl.tudelft.sem.models.StudentHouse;
import nl.tudelft.sem.models.User;
import nl.tudelft.sem.repositories.StudentHouseRepository;
import nl.tudelft.sem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HouseManagementService {
    private transient StudentHouseRepository studentHouseRepository;
    private transient UserRepository userRepository;

    public HouseManagementService(StudentHouseRepository studentHouseRepository,
                                  UserRepository userRepository) {
        this.studentHouseRepository = studentHouseRepository;
        this.userRepository = userRepository;
    }

    public StudentHouseRepository getStudentHouseRepository() {
        return studentHouseRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Method that adds a new housemate to the house of someone who already lives there
     * and updates the relevant repositories.
     *
     * @param resident  User that already lives in the house
     * @param housemate User that is the housemate-to-be of the resident
     */
    public void addHousemate(User resident, User housemate) {
        resident.addHousemate(housemate);
        studentHouseRepository.saveAndFlush(resident.getHouse());
        userRepository.saveAndFlush(housemate);
    }

    /**
     * Method that registers a User and updates the relevant repositories.
     *
     * @param userId    id of the User to register (passed by Authentication)
     */
    public void registerUser(int userId) {
        StudentHouse studentHouse = new StudentHouse();
        User user = new User(userId, studentHouse);
        userRepository.saveAndFlush(user);
    }

    /**
     * Method that handles joining a StudentHouse and updates the relevant repositories.
     *
     * @param house StudentHouse to join
     * @param user  User that wants to join this house
     */
    public void joinStudentHouse(StudentHouse house, User user) {
        house.addResident(user);
        studentHouseRepository.saveAndFlush(house);
        userRepository.saveAndFlush(user);
    }

    /**
     * Method to reset the database for the /reset mapping.
     */
    public void clearDatabase() {
        studentHouseRepository.deleteAll();
        studentHouseRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }
}
