package nl.tudelft.sem.registration;

import nl.tudelft.sem.exceptions.DatabaseException;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.repositories.UserRepository;

public interface Registrator {

    void setNext(Registrator handler);

    int handle(User user, UserRepository userRepository, int id)
            throws InvalidInputException, UserConflictException, DatabaseException;
}
