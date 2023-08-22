package nl.tudelft.sem.registration;

import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CheckConflicts extends BaseRegistrator {
    @Override
    public int handle(User user, UserRepository userRepository, int id)
            throws UserConflictException, InvalidInputException, DatabaseException {
        logger =  LoggerFactory.getLogger(Registrator.class);

        searchUser(user, userRepository);

        return super.checkNext(user, userRepository, id);
    }

    /**
     * Searches for the user in the database and throws an error if already found.
     *
     * @param user to be found
     * @param userRepository to be looked into
     * @throws UserConflictException when a user is found
     */
    public void searchUser(User user, UserRepository userRepository) throws UserConflictException {
        user.encode();

        if (userRepository.findByEmailOrUsername(user.getEmail(), user.getUsername()) != null) {
            logger.error("Registration failed: User with these credentials already exists!");
            throw new UserConflictException("Bad credentials: User already exists!");
        }
    }
}
