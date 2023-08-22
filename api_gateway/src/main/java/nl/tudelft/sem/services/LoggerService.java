package nl.tudelft.sem.services;

import nl.tudelft.sem.configuration.GatewayBeansConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  LoggerService provides logging functionality to all other classes
 *  on the Gateway. It is meant to be used as an implementation of the
 *  Singleton design pattern. It has a private instance of itself, private
 *  {@link Logger} object that does the actual logging, a private constructor and
 *  a public method getInstance() meant to call this constructor the first time
 *  it is called. The other methods of the {@link LoggerService} class are used
 *  by other classes on the singleton instance of the class to perform logging, whereas
 *  these methods call the corresponding methods of the {@link Logger} instance.
 */
public class LoggerService {

    private static final transient Logger actualLogger
            = LoggerFactory.getLogger("Service logger");

    private static transient LoggerService instance;


    /**
     * Private constructor for the LoggerService class. It is meant
     * to be called only by the static method getInstance() of the class
     * in order to ensure the Singleton design pattern usage of the LoggerService
     * class.
     */
    private LoggerService() {}

    /** This method is meant to be called by other classes that want to
     *  utilise the {@link LoggerService} class. It returns the singleton
     *  instance of the class, whereas the first time it is called it
     *  initializes it. Also, to ensure there are no concurrent-update threading
     *  issues, the code is synchronised using an {@link Object} object.
     *
     * @return The singleton instance of the {@link LoggerService} class.
     */
    public static LoggerService getInstance() {
        synchronized (GatewayBeansConfig.lock) {
            if (instance == null) {
                instance = new LoggerService();
            }
            return instance;
        }
    }

    /** Logs the provided information message, including as the source class the provided
     *  {@link Class} argument c. To do this, the method calls the info() method of the
     *  private {@link Logger} class field.
     *
     * @param message   The message to log.
     * @param c         The class to show as source in the log.
     */
    public void logInfo(String message, Class c) {
        actualLogger.info(message, c);
    }

    /** Logs the provided error message, including as the source class the provided
     *  {@link Class} argument c. To do this, the method calls the error() method of the
     *  private {@link Logger} class field.
     *
     * @param message   The message to log.
     * @param c         The class to show as source in the log.
     */
    public void logError(String message, Class c) {
        actualLogger.error(message, c);
    }


}
