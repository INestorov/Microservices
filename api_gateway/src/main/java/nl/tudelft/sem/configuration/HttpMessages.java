package nl.tudelft.sem.configuration;

public class HttpMessages {

    public static final String prefix = "Could not forward request! ";

    public static final String AUTHENTICATION = "Received request for authentication ";
    public static final String SUCCESSFUL_LOGIN = "Login was successful!";
    public static final String ERROR_NO_LOGIN = prefix + "User is not logged in!";
    public static final String ERROR_ALREADY_LOGGED_IN = prefix + "User is already logged in!";
    public static final String LOGOUT = "Session attribute deleted!";
    public static final String REQUEST_CREDITS = "Sending a request for user credits ...";
    public static final String REQUEST_HOUSE = "Sending a request for user's house id ...";

    public static final String HOUSE_MANAGEMENT = "Received request for house management ";

    public static final String FOOD_MANAGEMENT = "Received request for food management";

    public static final String RESET_RECEIVED =
            "Received request for resetting the whole application!";

    public static final String REGISTER_USER_IN_HOUSE =
            "Registering the new user in house management ...";
    public static final String CREATING_CREDITS_ACCOUNT =
            "Creating the credits account of the new user ...";
    public static final String ERROR_CANNOT_UNREGISTER_IN_A_HOUSE =
            "User cannot be unregistered: He/She is still a house member!";
    public static final String ERROR_DELETING_CREDITS_ACCOUNT =
            "The credits account of this user could not be removed!";
    public static final String ERROR_ILLEGAL_PARAMETERS =
            "The request contained userId or houseId parameter!";
}
