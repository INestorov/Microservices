package nl.tudelft.sem.services;

import java.util.ArrayList;
import java.util.List;
import javax.security.auth.DestroyFailedException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.registration.InitializeChain;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {
    private final transient UserRepository userRepository;
    static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

    public UserAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user and handles the chain of responsibility.
     *
     * @param user the user to be registered
     * @return ResponseEntity[String] containing the generated ID of the new user
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ResponseEntity<String> registerNewUser(User user) {
        return InitializeChain.handle(user, this.userRepository, -1, logger);
    }

    /**
     * Checks if the user exists in the database and if the password is correct. This method
     * also handles verifying the encoded password.
     *
     * @param userLog is the user who wants to log in
     * @return the ID of the user, or null if the user does not exist
     */
    public Integer login(User userLog) {
        try {
            User user = userRepository.findByUsername(userLog.getUsername());

            if (user.matches(userLog.getPassword())) {
                logger.info("Login successful.");
                return user.getId();
            }

            logger.error("Bad credentials: Password incorrect.");
            return null;
        } catch (Exception e) {
            logger.error("Bad credentials: Username does not exist.");
            return null;
        }
    }

    /**
     * Unregisters a user by removing it from the database.
     *
     * @param user is the user to be removed
     * @return boolean containing success value
     */
    public boolean unregisterUser(User user) {
        User userToDelete = userRepository.findByUsername(user.getUsername());
        if (userToDelete == null) {
            logger.error("No user with the provided username was found!");
            return false;
        }

        if (!userToDelete.matches(user.getPassword())) {
            logger.error("Incorrect password.");
            return false;
        }

        try {
            userRepository.delete(userToDelete);
        } catch (Exception e) {
            logger.error("User could not be unregistered.");
            return false;
        }

        logger.info("User with username " + user.getUsername() + " was successfully deleted.");
        return true;
    }

    /**
     * Takes a list of usernames and fetches their corresponding ids from the database
     *      using a {@link UserRepository} bean.
     *
     * @param usernames The list of usernames whose ids to fetch.
     * @return  A list of ids fetched from the database.
     * @throws InvalidInputException Throws it if either the database querying raised
     *      an exception or if the number of fetched ids is different from the number
     *      of provided usernames.
     */
    public List<Integer> getIdsForUsernames(List<String> usernames) throws InvalidInputException {
        try {
            logger.info("Fetching users with provided usernames ...");
            List<User> foundUsers = userRepository.findAllUsersByUsernames(usernames);

            if (foundUsers.size() != usernames.size()) {
                logger.error("Number of fetched ids does not match number of provided usernames!");
                throw new InvalidInputException("Number of user ids fetched is different"
                        + " from the number of provided usernames! Either a name was duplicated "
                        + "or one of the usernames did not exist");
            }

            logger.info("Users fetched successfully! Creating the id list...");

            List<Integer> foundIds = new ArrayList<>();
            for (User user : foundUsers) {
                foundIds.add(user.getId());
            }

            return foundIds;
        } catch (Exception e) {
            logger.error("The fetching of the users failed!");
            throw new InvalidInputException(e.getMessage());
        }
    }

    /** Deletes all users from the database.
     *
     * @throws DestroyFailedException Throws it if the operation fails.
     */
    public void clearDatabase() throws DestroyFailedException {
        logger.info("Beginning the clearing of the database ...");

        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            logger.error("Database could not be reset!");
            throw new DestroyFailedException("The clearing of the user database failed!");
        }
    }
}
