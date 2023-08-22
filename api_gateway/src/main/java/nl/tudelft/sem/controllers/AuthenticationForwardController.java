package nl.tudelft.sem.controllers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nl.tudelft.sem.configuration.HttpMessages;
import nl.tudelft.sem.configuration.Microservice;
import nl.tudelft.sem.requests.adapters.RequestObjectsAdapter;
import nl.tudelft.sem.requests.adapters.ResponseObjectsAdapter;
import nl.tudelft.sem.requests.senders.HouseIdRequestSender;
import nl.tudelft.sem.requests.senders.RequestSender;
import nl.tudelft.sem.services.LoggerService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("application/authentication")
public class AuthenticationForwardController {

    public static final String userId = "userId";
    public static final String houseId = "houseId";
    public static final String encounter = " encountered: ";

    private static final LoggerService LOGGER_SERVICE = LoggerService.getInstance();


    final transient HouseIdRequestSender houseIdRequestSender;
    final transient RequestSender requestSender;

    /**
     *  Constructor for our request.
     *
     * @param houseIdRequestSender The bean which is used for
     *                              getting the houseId of the user
     * @param requestSender    The {@link RequestSender} object used
     *                              for making requests.
     */
    public AuthenticationForwardController(HouseIdRequestSender houseIdRequestSender,
                                           RequestSender requestSender) {
        this.houseIdRequestSender = houseIdRequestSender;
        this.requestSender = requestSender;
    }

    private void logError(String message) {
        LOGGER_SERVICE.logError(message, this.getClass());
    }

    private void logInfo(String message) {
        LOGGER_SERVICE.logInfo(message, this.getClass());
    }

    /**
     * Receives a POST request for registering a new user. Then
     * it forwards the received request to the authentication microservice using the
     * {@link RequestObjectsAdapter} class to create the new request. It then sends it
     * to the microservice usign an {@link HttpClient} bean object of the controller class.
     * If the microservice returns one of the success HTTP status codes, then the operation
     * succeeded. In that case, the Gateway again uses the {@link HttpClient} bean to
     * perform two more requests - one for adding the new user to the house management
     * microservice and one for creating the user's credit account to the food management
     * microservice. Then it returns a {@link ResponseEntity} object for an ACCEPTED HTTP response.
     * If the authentication microservice returns an error status code, then the Gateway returns
     * a {@link ResponseEntity} object for a response with the same status code and error
     * message to the user.
     *
     * @param session The {@link HttpSession} session object created for the user if he does already
     *                have one.
     * @param request The {@link HttpServletRequest} object created by the servlet container
     *                ( Tomcat ), that encapsulates the info about the incoming request.
     * @return A {@link ResponseEntity} object indicating if the operation succeeded.
     */
    @PostMapping("sign-up")
    public ResponseEntity<String> forwardRegisteringRequest(HttpSession session,
                                                            HttpServletRequest request) {

        logInfo(HttpMessages.AUTHENTICATION + "sign-up" + " ...");

        try {

            // Get the response | Handle exceptions
            HttpResponse<String> response = requestSender.forwardRequest(
                    request,
                    Microservice.AUTHENTICATION.url,
                    "sign-up"
            );

            // Check if the status code is a success type
            if (!List.of(200, 201, 202).contains(response.statusCode())) {
                return ResponseObjectsAdapter.createResponse(response);
            }

            logInfo(HttpMessages.REGISTER_USER_IN_HOUSE);

            // Send a request for adding the user to house management
            requestSender.sendRequestWithNoBody(
                    HttpMethod.POST,
                    Microservice.HOUSE_MANAGEMENT.url,
                    "house/registerUser" + "?userId=" + response.body());

            logInfo(HttpMessages.CREATING_CREDITS_ACCOUNT);

            // Send a request for creating the new user's credit account
            //      on food management
            requestSender.sendRequestWithNoBody(
                    HttpMethod.POST,
                    Microservice.FOOD_MANAGEMENT.url,
                    "credits/addCreditAccount" + "?userId=" + response.body());

            // Return an ACCEPTED HTTP response
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response.body());

        } catch (IOException | InterruptedException e) {
            // In case of an exception return a BAD Gateway response with the error message
            logError(e.getClass().getName() + encounter + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    e.getClass().getSimpleName() + encounter + e.getLocalizedMessage());
        }

    }

    /**
     * Receives a POST request for logging in and reroutes it to the authentication
     * microservice. It first checks if the user is already logged in and if so, sends
     * a bad request response, notifying the user of this. Then creates a forwarding
     * request and sends it, handling any exceptions that could be thrown. If the
     * authentication microservice responds that the logging in was successful, the gateway
     * saves the user id in the response in a session and sends a response ACCEPTED to the
     * user. If not, the user is notified accordingly by the error message received in the
     * response of the microservice.
     *
     * @param session The {@link HttpSession} session object created for the user if he does already
     *                have one.
     * @param request The {@link HttpServletRequest} object created by the servlet container
     *                ( Tomcat ), that encapsulates the info about the incoming request.
     * @return A {@link ResponseEntity} object indicating if the login was successful or
     *      if an error was encountered.
     */
    @PostMapping("login")
    public ResponseEntity<String> forwardLogin(HttpSession session,
                                               HttpServletRequest request) {

        logInfo(HttpMessages.AUTHENTICATION + " login ...");

        if (session.getAttribute(userId) != null) {
            logError(HttpMessages.ERROR_ALREADY_LOGGED_IN);
            return ResponseEntity.badRequest().body(
                    HttpMessages.ERROR_ALREADY_LOGGED_IN);
        }

        // The user is not already logged in
        try {

            // Send a request for logging in
            HttpResponse<String> response = requestSender.forwardRequest(
                    request,
                    Microservice.AUTHENTICATION.url,
                    "login"
            );

            if (!List.of(200, 201, 202).contains(response.statusCode())) {
                return ResponseObjectsAdapter.createResponse(response);
            }

            // The login was successful
            logInfo(HttpMessages.SUCCESSFUL_LOGIN);
            session.setAttribute(userId, response.body());

            // Attempt to get the user credits
            houseIdRequestSender.requestHouseId(session);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpMessages.SUCCESSFUL_LOGIN);

        } catch (IOException e) {
            session.removeAttribute(userId);
            logError(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    "IO Exception encountered: " + e.getLocalizedMessage());

        } catch (InterruptedException e) {
            session.removeAttribute(userId);
            logError("Interrupted Exception encountered: " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    e.getClass().getSimpleName() + encounter + e.getLocalizedMessage()
            );

        }
    }


    /**
     * Receives a request and forwards it to the authentication microservice.
     * First, the method checks if the user has a stored user id in his {@link HttpSession}
     * session object. If not, the Gateway knows the user was not logged in and returns a
     * {@link ResponseEntity} object for an error response. If yes, the method creates a forwarding
     * request and sends it to the authentication microservice, using an {@link HttpClient} bean
     * object, handling any exceptions that might be thrown. After that, it returns a response to
     * the user containing the information that was returned by the microservice in a
     * {@link ResponseEntity} object for an ACCEPTED response. If an exception is thrown in
     * the method, it is handled by returning a {@link ResponseEntity} object for a BAD_GATEWAY
     * response to the user with the error message.
     *
     * @param session  The {@link HttpSession} session object created for the user if he does
     *                 already have one.
     * @param request  The {@link HttpServletRequest} object created by the servlet container
     *                 ( Tomcat ), that encapsulates the info about the incoming request.
     * @param function A path variable that is part of the request url.
     * @return A {@link ResponseEntity} object indicating if the login was successful or
     *      if an error was encountered.
     */
    @RequestMapping("{function}")
    public ResponseEntity<String> forwardAuthenticationRequest(HttpSession session,
                                                               HttpServletRequest request,
                                                               @PathVariable String function) {

        logInfo(HttpMessages.AUTHENTICATION + function + " ...");

        // If the user wants to logout, just remove his session attributes
        if (function.equals("logout")) {

            session.removeAttribute(userId);
            session.removeAttribute(houseId);

            logInfo(HttpMessages.LOGOUT);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(HttpMessages.LOGOUT);

        } else {

            try {

                // If this is a request for unregistering ...
                if (function.equals("unregister")) {

                    // If the user is a member of a house, decline the request
                    if (session.getAttribute("houseId") != null) {

                        logError(HttpMessages.ERROR_CANNOT_UNREGISTER_IN_A_HOUSE);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                HttpMessages.ERROR_CANNOT_UNREGISTER_IN_A_HOUSE
                        );
                    }

                    // Send a request for removing the credits account
                    HttpResponse<String> rmCreditsResponse = requestSender.sendRequestWithNoBody(
                            HttpMethod.DELETE,
                            Microservice.FOOD_MANAGEMENT.url,
                            "credits/removeCreditAccount" + "?userId="
                                    + session.getAttribute("userId")
                    );

                    if (!List.of(200, 201, 202).contains(rmCreditsResponse.statusCode())) {
                        logError(HttpMessages.ERROR_DELETING_CREDITS_ACCOUNT);
                        return ResponseObjectsAdapter.createResponse(rmCreditsResponse);
                    }

                    // Delete the session attributes
                    logInfo(HttpMessages.LOGOUT);
                    session.removeAttribute(userId);
                }

                // Send the new forward request and get the response | catch exception
                HttpResponse<String> response = requestSender.forwardRequest(
                        request,
                        Microservice.AUTHENTICATION.url,
                        function
                );

                // Check if the response status code is a success type
                if (!List.of(200, 201, 202).contains(response.statusCode())) {
                    return ResponseObjectsAdapter.createResponse(response);
                }

                // Return an ACCEPTED response if the operation succeeded
                logInfo(response.body());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response.body());

            } catch (IOException | InterruptedException e) {
                logError(e.getLocalizedMessage());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                        e.getClass().getSimpleName() + encounter + e.getLocalizedMessage());
            }

        }


    }


}
