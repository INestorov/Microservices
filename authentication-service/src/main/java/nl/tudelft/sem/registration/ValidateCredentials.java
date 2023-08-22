package nl.tudelft.sem.registration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.LoggerFactory;

public class ValidateCredentials extends BaseRegistrator {

    @Override
    public int handle(User user, UserRepository userRepository, int id)
            throws InvalidInputException, UserConflictException, DatabaseException {
        logger = LoggerFactory.getLogger(Registrator.class);

        verifyUsername(user.getUsername());
        verifyPassword(user.getPassword());
        verifyEmail(user.getEmail());

        return super.checkNext(user, userRepository, id);
    }

    /**
     * Verifies the validity of the username.
     *
     * @param username contains the username to be checked
     * @throws InvalidInputException is thrown with explanation as the message
     */
    public static void verifyUsername(String username) throws InvalidInputException {
        if (username == null) {
            throw new InvalidInputException("No username was entered.");
        }
        if (username.length() < 8 || username.length() > 24) {
            throw new InvalidInputException("Username should be between 8 and 24 characters.");
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            logger.error("Username invalid.");
            throw new InvalidInputException("Username should only contain letters and numbers.");
        }
    }

    /**
     * Verifies the validity of the password. Can only be used for registration procedure!
     *
     * @param password contains the password to be checked
     * @throws InvalidInputException is thrown with explanation as the message
     */
    public static void verifyPassword(String password) throws InvalidInputException {
        if (password == null) {
            throw new InvalidInputException("No password was entered.");
        }
        Pattern pattern = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^!&-+=()]).{8,24}$");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            logger.error("Password invalid.");
            throw new InvalidInputException("Password must contain at least one lowercase, "
                    + "and one uppercase character, as well as one digit and one of the characters "
                    + "@#$%^!&-+=(). The password must be at least 8 characters long and shorter "
                    + "than 24 characters.");
        }
    }

    /**
     * Verifies the validity of the email.
     *
     * @param email contains the email to be checked
     * @throws InvalidInputException is thrown with explanation as the message
     */
    public static void verifyEmail(String email) throws InvalidInputException {
        if (email == null) {
            throw new InvalidInputException("No email address entered.");
        }
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            logger.error("Email invalid.");
            throw new InvalidInputException("This email address is not a valid email address.");
        }
    }
}
