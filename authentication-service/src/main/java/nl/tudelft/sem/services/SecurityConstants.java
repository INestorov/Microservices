package nl.tudelft.sem.services;

public class SecurityConstants {
    public static final String SECRET = "ThisBeTheSecretKey";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/application/authentication/sign-up";
    public static final String LOG_IN_URL = "/application/authentication/login";
    public static final String UNSIGN_URL = "/application/authentication/unregister";
    public static final String RESET_URL = "/application/authentication/reset";
    public static final String GETIDS_URL = "/application/authentication/user/get_names";

}
