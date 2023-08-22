package nl.tudelft.sem.exceptions;

public class DatabaseException extends Exception {
    public static final long serialVersionUID = 43287341;

    public DatabaseException(String errorMessage) {
        super(errorMessage);
    }
}