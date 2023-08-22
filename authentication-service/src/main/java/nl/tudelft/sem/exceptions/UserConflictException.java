package nl.tudelft.sem.exceptions;

public class UserConflictException extends Exception {
    public static final long serialVersionUID = 43287342;

    public UserConflictException(String errorMessage) {
        super(errorMessage);
    }
}

