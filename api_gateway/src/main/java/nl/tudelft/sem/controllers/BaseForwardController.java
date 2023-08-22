package nl.tudelft.sem.controllers;



import java.net.http.HttpResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.configuration.Microservice;
import nl.tudelft.sem.requests.adapters.ResponseObjectsAdapter;
import nl.tudelft.sem.requests.senders.RequestSender;
import nl.tudelft.sem.services.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 *      An abstract class, meant as a base for other classes that are
 * annotated with the {@link org.springframework.stereotype.Controller}
 * annotation. It provides basic operations which are part of the forwarding
 * requests functionality that can be easily used by its subclasses to forward
 * an intercepted {@link HttpServletRequest} request object. The class contains:
 * 1) A {@link RequestSender} object used for the actual sending of requests
 * 2) A {@link LoggerService} static object which should be used by subclasses
 *      for logging information and error messages.
 * The main functionality this base class provides to its subclasses is :
 * 1) {@link LoggerService} object for logging in subclasses.
 * 2) Actual sending of forwarded requests.
 * 3) Catching thrown {@link Exception} objects during the sending of the
 *      requests, reading of the body of the input {@link HttpServletRequest}
 *      object or any other operation in the subclasses used methods.
 * 4) Checking the status code of the responses received back and forwarding
 *      back error responses to the sender of the initially received request.
 * Classes which extend this base class should implement 3 methods. One for
 * setting the microservice they will forward to. One for logging a message
 * for which microservice a request has been received. And one for possible
 * further processing of the response returned by the microservice to the
 * forwarded request. Also subclasses of this class should generally not
 * have more than one endpoint for forwarding requests, as this class does not
 * provide a way to have different endpoint methods with differences in their
 * processing of the response.
 *
 */
public abstract class BaseForwardController {

    protected final transient RequestSender requestSender;
    protected final transient LoggerService logger;

    /** A simple constructor for the {@link BaseForwardController} class.
     * Sets the {@link RequestSender} object used for sending the actual
     * forward requests. Also, it uses the Singleton {@link LoggerService}
     * class to initialize its inner logger object.
     *
     * @param requestService    {@link RequestSender} object used for sending
     *                              the actual forward requests.
     */
    protected BaseForwardController(RequestSender requestService) {
        this.requestSender = requestService;
        this.logger = LoggerService.getInstance();
    }

    /** Accepts an {@link HttpServletRequest} object and a {@link String} parameter
     * which should be the path to append to the {@link Microservice} object url.
     * The method uses the inner {@link RequestSender} object to send the forward
     * request using the provided parameters and the getMicroservice() method
     * which should be implemented by the subclasses to indicate the microservice
     * to which the forwarded request is sent. Then the method calls the private
     * processResponseBase method to make some general processing of the returned
     * {@link HttpResponse} object.
     *
     * @param request   The {@link HttpServletRequest} object to pass to the used methods
     *                      for sending the forward request and processing the response.
     * @param path      The path to append to the address of the microservice returned
     *                      from the getMicroservice() method and then pass to the
     *                  {@link RequestSender} object method.
     * @return          A {@link ResponseEntity} returned either from the processResponseBase
     *                      method or after an exception is handled.
     */
    protected ResponseEntity<String> sendForwardRequest(HttpServletRequest request,
                                                      String path) {
        logger.logInfo(
                receivedRequestMessage() + " " + path,
                this.getClass()
        );

        try {
            HttpResponse<String> response = requestSender.forwardRequest(
                    request,
                    getMicroservice().url,
                    path
            );

            return processResponseBase(response, request);

        } catch (Exception e) {
            logger.logError(
                    "Exception encountered: " + e.getLocalizedMessage(),
                    this.getClass()
            );
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getLocalizedMessage());
        }
    }

    /** This method is called after a request is forwarded and a response is received.
     * It performs a basic processing of the response by checking its status code and
     * returning a Bad Gateway {@link ResponseEntity} response object in case the status
     * code is an error one. The error response returned contains in that case the error
     * message returned by the microservice. Otherwise, the method calls the abstract
     * processResponse() method that is implemented by subclasses to further process the
     * response {@link HttpResponse} object and then returns an ACCEPTED {@link ResponseEntity}
     * object if no exception was thrown in the processResponse() method.
     *
     * @param response  An {@link HttpResponse} object representing the HTTP response
     *                      of the microservice to which a request was forwarded.
     *
     * @param servletRequest  An {@link HttpServletRequest} object representing the
     *                             initially received and forwarded request.
     * @return  A {@link ResponseEntity} object which is basically a forwarded response
     *              from the microservice or indicates an error happened on the Gateway.
     *
     */
    private ResponseEntity<String> processResponseBase(
            HttpResponse<String> response,
            HttpServletRequest servletRequest) {

        if (!List.of(200, 201, 202).contains(response.statusCode())) {
            return ResponseObjectsAdapter.createResponse(response);
        }

        processResponse(response, servletRequest);

        return ResponseEntity.accepted().body(response.body());
    }

    /** This method should be implemented by subclasses if they
     * want to perform some additional processing of the returned
     * responses from the microservice beyond status code check.
     *
     * @param response  An {@link HttpResponse} object representing the HTTP response
     *                      of the microservice to which a request was forwarded.
     *
     * @param servletRequest  An {@link HttpServletRequest} object representing the
     *                              initially received and forwarded request.
     */
    protected abstract void processResponse(HttpResponse<String> response,
                                       HttpServletRequest servletRequest);

    /** This method should be implemented by subclasses to return the
     * {@link Microservice} enum object that represents the microservice
     * to which all requests in the subclass should be forwarded and its
     * corresponding URL address.
     *
     * @return A {@link Microservice} object representing the microservice
     *              to which requests are forwarded by the subclass.
     */
    protected abstract Microservice getMicroservice();

    /** This method should be implemented by subclasses to return
     * a message to be logged when a request is received by the subclass
     * endpoint method.
     *
     * @return A {@link String} log message that indicates to which
     *              microservice the received request should be
     *              forwarded.
     */
    protected abstract String receivedRequestMessage();
}
