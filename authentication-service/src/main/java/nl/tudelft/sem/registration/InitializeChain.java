package nl.tudelft.sem.registration;

import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

public class InitializeChain {
    /**
     * Static method to initialize the chain of responsibility for the registration procedure.
     *
     * @param user to be verified and registered
     * @param userRepository is the repository with all the users
     * @param id of the user in the database, contains -1 until user is successfully registered.
     * @return id
     * @throws InvalidInputException if input is invalid
     * @throws UserConflictException if user already exists
     * @throws DatabaseException if user could not be saved to database
     */
    public static ResponseEntity<String> handle(User user, UserRepository userRepository, int id,
                                                Logger logger) {
        // Initialize the chain of responsibility.
        Registrator vc = new ValidateCredentials();
        CheckConflicts cc = new CheckConflicts();
        RegisterUser ru = new RegisterUser();
        cc.setNext(ru);
        vc.setNext(cc);

        try {
            int retId = vc.handle(user, userRepository, id);
            if (retId == -1) {
                logger.error("Unknown error. Returned id was -1.");
                return ResponseEntity.status(400).body("Error. Please try again.");
            }
            return ResponseEntity.status(201).body(id + "");
        } catch (InvalidInputException e) {
            logger.error(e.getLocalizedMessage());
            return ResponseEntity.status(406).body("Bad credentials: " + e.getLocalizedMessage());
        } catch (DatabaseException e) {
            logger.error(e.getLocalizedMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (UserConflictException e) {
            logger.error(e.getLocalizedMessage());
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
