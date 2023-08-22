package nl.tudelft.sem.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.models.StudentHouse;
import nl.tudelft.sem.models.User;
import nl.tudelft.sem.repositories.UserRepository;
import nl.tudelft.sem.services.HouseManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contains the following methods.
 * - create a house
 * - delete a house
 * - join a house
 * - leave a house
 * - get house's id from user id
 * - get a list of all residents
 * - reset the database
 * - validate if users are housemates
 */

@RestController
@RequestMapping({"/application/housemanagement/house"})
public class StudentHouseController {
    static final Logger logger = LoggerFactory.getLogger(StudentHouseController.class);
    final transient HouseManagementService houseManagementService;
    public final transient String userIdString = "userId";
    public final transient String loggerUserIdString = "User with id ";
    public final transient String noUserString = "There is no user with id ";

    public StudentHouseController(HouseManagementService houseManagementService) {
        this.houseManagementService = houseManagementService;
    }

    /**
     * Post Mapping to add a User and save it to User Repository.
     *
     * @param userId id of the User to add
     * @return HTTP OK status message if successfully completed
     */
    @PostMapping("/registerUser")
    @ResponseBody
    public ResponseEntity<String> registerUser(@RequestParam(userIdString) Integer userId) {
        logger.info(": Received a POST-request for adding a user.");
        try {
            houseManagementService.registerUser(userId);
            logger.info(": Received a POST-request for adding a user.");
            return ResponseEntity.ok(loggerUserIdString + userId + " was added successfully");
        } catch (Exception e) {
            logger.error("This did not work as expected.");
            return ResponseEntity.badRequest()
                    .body(e.getMessage() + "- This did not work as expected");
        }
    }

    /**
     * Post Mapping to create a new house and store it in the database.
     *
     * @param houseName name of house to create
     * @return HTTP OK status message if successfully completed
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createStudentHouse(@RequestParam("houseName") String houseName) {
        logger.info(": Received a POST-request for creating a new student house.");
        try {
            logger.info("StudentHouse name: " + houseName);
            StudentHouse newStudentHouse = new StudentHouse(houseName);
            houseManagementService.getStudentHouseRepository().save(newStudentHouse);
            return ResponseEntity.ok(newStudentHouse.toString()
                    + " was created successfully, id " + newStudentHouse.getHouseId());
        } catch (Exception e) {
            logger.error("This did not work as expected.");
            return ResponseEntity.badRequest()
                    .body(e.getMessage() + "- This did not work as expected");
        }
    }

    /**
     * Delete Mapping to delete a house from the database.
     *
     * @param houseId id of the house to delete
     * @return HTTP OK status message if deletion successful
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteStudentHouse(@RequestParam("houseId") int houseId) {
        logger.info(": Received a DELETE-request for student house.");
        Optional<StudentHouse> studentHouseOptional =
                houseManagementService.getStudentHouseRepository().findById((int) houseId);
        if (studentHouseOptional.isPresent()) {
            houseManagementService.getStudentHouseRepository()
                    .delete(studentHouseOptional.get());
            return ResponseEntity.ok(studentHouseOptional
                    .get().toString() + " was deleted successfully");
        } else {
            logger.error("There was no student house to delete");
            return ResponseEntity.badRequest()
                    .body("No studentHouse for this id, thus no studentHouse to be deleted");
        }
    }

    /**
     * Post Mapping to let a user join a certain student house.
     *
     * @param houseName name of house to join
     * @param userId    id of the user that wants to join this house
     * @return HTTP OK status message if user has joined house successfully
     */
    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<String> joinStudentHouse(@RequestParam("houseName") String houseName,
                                                   @RequestParam(userIdString) int userId) {
        logger.info(": Received a POST-request for a user joining a house.");
        User currentUser = houseManagementService.getUserRepository()
                .findById(userId).orElse(null);
        StudentHouse studentHouse = houseManagementService.getStudentHouseRepository()
                .findByName(houseName);
        if (studentHouse != null && currentUser != null) {
            houseManagementService.joinStudentHouse(studentHouse, currentUser);
            logger.info(loggerUserIdString + userId
                    + " joined the house with name " + studentHouse.getName());
            return ResponseEntity.ok(loggerUserIdString + userId
                    + " successfully joined the house with name " + studentHouse.getName()
                    + "and id " + studentHouse.getHouseId());
        } else if (studentHouse == null) {
            logger.error("There was no student house to join with name " + houseName);
            return ResponseEntity.badRequest().body("This student house does not exist");
        } else {
            logger.error(noUserString + userId);
            return ResponseEntity.badRequest().body("This user does not exist");
        }
    }

    /**
     * Post Mapping to let a user leave a certain student house.
     *
     * @param userId id of the user that wants to leave his/her house
     * @return HTTP OK status message if user has joined house successfully
     */
    @PostMapping("/leave")
    @ResponseBody
    public ResponseEntity<String> leaveStudentHouse(@RequestParam(userIdString) int userId) {
        logger.info(": Received a POST-request for a user leaving a house.");
        User currentUser = houseManagementService.getUserRepository()
                .findById(userId).orElse(null);
        StudentHouse studentHouse;
        if (currentUser != null) {
            studentHouse = currentUser.getHouse();
            if (studentHouse != null) {
                studentHouse.removeResident(currentUser);
                houseManagementService.getStudentHouseRepository()
                        .saveAndFlush(studentHouse);
                houseManagementService.getUserRepository().saveAndFlush(currentUser);
                logger.info(loggerUserIdString + userId
                        + " left the house with name " + studentHouse.getName());
                return ResponseEntity.ok(loggerUserIdString + currentUser.getUserId()
                        + "successfully left the house with name " + studentHouse.getName());
            } else {
                logger.error("There was no student house to leave for user with id "
                        + userId);
                return ResponseEntity.badRequest()
                        .body("This student house does not exist");
            }
        } else {
            logger.error(noUserString + userId);
            return ResponseEntity.badRequest().body("This user does not exist");
        }
    }

    /**
     * Get Mapping to get houseId of the house a user lives in.
     *
     * @param userId user to get houseId from
     * @return house's id with getHouseId() method, -1 if userId doesn't exist
     */
    @GetMapping("/getId")
    @ResponseBody
    public ResponseEntity<String> getStudentHouseId(@RequestParam(userIdString) int userId) {
        logger.info(": Received a GET-request for finding out which house a user lives in.");
        User user = houseManagementService.getUserRepository().findById(userId).orElse(null);
        if (user != null) {
            logger.info("Successful: user with id " + userId
                    + " lives in house with id " + user.getHouse().getHouseId());
            return ResponseEntity.ok(user.getHouse().getHouseId() + "");
        } else {
            logger.error(noUserString + userId);
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Receives a DELETE request for clearing the whole database.
     *
     * @return HTTP OK status message if house management database was reset successfully
     */
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        logger.info("Received a DELETE-request for resetting the database.");
        try {
            houseManagementService.clearDatabase();
            logger.info("Cleared the house management database");
            return ResponseEntity.ok().body("The clearing of the database was successful!");
        } catch (Exception e) {
            logger.error("Exception caught, couldn't clear database");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getLocalizedMessage());
        }
    }

    /**
     * Method for food management to validate
     * if users live in the same house as the cook when a meal is shared.
     *
     * @param eatersId list of user ids for whom it needs to be checked
     *                 if they live in the same house
     * @param cookId userId of the cook
     * @return list with users that live in the same house as the cook
     */
    @GetMapping("/validatehousemates")
    public List<Integer> validateHousemates(@RequestParam("eaters") List<Integer> eatersId,
                                         @RequestParam(userIdString) int cookId) {
        logger.info(": Received a GET-request for validating if users are housemates.");
        User cook = houseManagementService.getUserRepository()
                .findById(cookId).orElse(null);
        List<Integer> validatedId = new ArrayList<>();
        if (cook != null) {
            logger.info("Checked if users are housemates");
            for (int userId : eatersId) {
                User user = houseManagementService.getUserRepository()
                        .findById(userId).orElse(null);
                if (user != null) {
                    if (user.getHouse().equals(cook.getHouse())) {
                        validatedId.add(userId);
                    }
                } else {
                    logger.error("A user id in this list does not exist");
                }
            }
            return validatedId;
        } else {
            logger.error(loggerUserIdString + cookId + " doesn't exist, so no housemates");
            validatedId.add(-1);
            return validatedId;
        }
    }

    /**
     * Post Mapping to let a user add a housemate to his house.
     *
     * @param userId      id of user that already lives in the house
     * @param housemateId id of the user that the user wants to add as housemate
     * @return HTTP OK status message if user was added successfully
     */
    @PostMapping("/addhousemate")
    @ResponseBody
    public ResponseEntity<String> addHousemate(@RequestParam(userIdString) int userId,
                                               @RequestParam("houseMateId") int housemateId) {
        logger.info(": Received a POST-request for a user adding a housemate.");
        User resident = houseManagementService.getUserRepository()
                .findById(userId).orElse(null);
        User housemate = houseManagementService.getUserRepository()
                .findById(housemateId).orElse(null);
        if (resident != null && housemate != null) {
            houseManagementService.addHousemate(resident, housemate);
            logger.info(loggerUserIdString + housemateId
                    + " was added to the house of user with id " + userId);
            return ResponseEntity.ok(loggerUserIdString + housemateId
                    + " was added to the house of user with id " + userId);
        } else if (resident == null) {
            logger.error(noUserString + userId);
            return ResponseEntity.badRequest().body("This resident does not exist");
        } else {
            logger.error(noUserString + housemateId);
            return ResponseEntity.badRequest().body("This possible housemate does not exist");
        }
    }

    /**
     * Method to get all housemates of a user.
     *
     * @param userId id of the user to get all housemates of
     * @return JSON int array with users that live in the same house as the user with id userId,
     *         including the user with id userId
     */
    @GetMapping("/gethousemates")
    public String getHousemates(@RequestParam(userIdString) int userId) {
        logger.info(": Received a GET-request for getting all housemates of users.");
        User user = houseManagementService.getUserRepository()
                .findById(userId).orElse(null);
        if (user != null) {
            logger.info("User found, looking for his StudentHouse");
            StudentHouse studentHouse = user.getHouse();
            if (studentHouse != null) {
                logger.info("StudentHouse found, returned all Users living in the house");
                List<User> userList;
                userList = studentHouse.getResidents();
                String resultJson = "[" + userList.get(0).getUserId();
                for (int i = 1; i < userList.size(); i++) {
                    resultJson = resultJson + "," + userList.get(i).getUserId();
                }
                resultJson += "]";
                return resultJson;
            } else {
                logger.error(loggerUserIdString + userId
                        + " doesn't live in a house, so no housemates");
                return null;
            }
        } else {
            logger.error(loggerUserIdString + userId + " doesn't exist, so no housemates");
            return null;
        }
    }

}
