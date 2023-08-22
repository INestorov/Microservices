package nl.tudelft.sem.exceptions;

public class InvalidInputException extends Exception {
    public static final long serialVersionUID = 43287343;

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
