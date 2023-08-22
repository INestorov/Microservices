package nl.tudelft.sem.registration;

import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.slf4j.Logger;

// Deprecated warning.
@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
public abstract class BaseRegistrator implements Registrator {
    private transient Registrator next;
    protected static transient Logger logger;

    public void setNext(Registrator r) {
        this.next = r;
    }

    protected int checkNext(User user, UserRepository userRepository, int id)
            throws InvalidInputException, UserConflictException, DatabaseException {
        if (next == null) {
            return id;
        }
        return next.handle(user, userRepository, id);
    }
}
