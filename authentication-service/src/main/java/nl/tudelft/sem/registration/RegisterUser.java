package nl.tudelft.sem.registration;

import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.LoggerFactory;

public class RegisterUser extends BaseRegistrator {

    @Override
    public int handle(User user, UserRepository userRepository, int id)
            throws UserConflictException, InvalidInputException, DatabaseException {
        logger = LoggerFactory.getLogger(Registrator.class);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Could not save the user to the database.");
            throw new DatabaseException("Registration failed: Could not save the new user"
                    + "in the database" + e.getLocalizedMessage());
        }

        id = userRepository.findByUsername(user.getUsername()).getId();
        logger.info("Succesfully saved the user to the database.");

        return super.checkNext(user, userRepository, id);
    }
}
