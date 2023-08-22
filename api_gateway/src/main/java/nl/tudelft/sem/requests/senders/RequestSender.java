package nl.tudelft.sem.requests.senders;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.requests.adapters.RequestObjectsAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class RequestSender {

    final transient HttpClient httpClient;

    /**
     * Creates a new {@link RequestSender} object with the provided {@link HttpClient}
     *  object to use for sending requests.
     *
     * @param httpClient    The {@link HttpClient} object used for sending requests.
     */
    public RequestSender(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    /** Sends a request using the information in the provided {@link HttpServletRequest}
     *  object. The url of the sent request is the provided Microservice address
     *  plus the provided path plus the query information in the {@link HttpServletRequest}
     *  provided object. The body is again that of the provided request.
     *
     * @param servletRequest An {@link HttpServletRequest} object. Its body and query
     *                       parameters are used to make the new request.
     * @param microserviceAddress   The address of the microservice to which the request
     *                              is sent.
     * @param path  The path to append to the microservice address argument in order to
     *              get the new url of the created request.
     * @return      An {@link HttpResponse} object with a {@link String} body, which
     *              is received in response to the newly created and sent request.
     *
     * @throws IOException  Throws it if the getting of the body or the sending of the
     *                          new request throws such an exception.
     * @throws InterruptedException Throws it if the sending of the new request throws
     *                                  such an exception.
     */
    public HttpResponse<String> forwardRequest(HttpServletRequest servletRequest,
                                               String microserviceAddress,
                                               String path)
            throws IOException, InterruptedException {

        String body = getRequestBody(servletRequest);

        HttpRequest request = RequestObjectsAdapter.change(
                servletRequest,
                microserviceAddress + path,
                "dwd",
                servletRequest.getMethod(),
                body);

        return httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }

    /** Sends a request without a body, with the provided method, to the URL formed
     *      by combining the provided microservice address and path. The response
     *      from this request is returned from the method as a {@link HttpResponse}
     *      object with a {@link String} body type.
     *
     * @param method    The method of the new request.
     * @param microserviceAddress   The address of the microservice to which the request
     *                                  is sent.
     * @param pathAndQuery          The path appended to the provided microservice address
     *                                  in order to form the url of the new request.
     * @return  An {@link HttpResponse} object with a {@link String} body type.
     * @throws IOException  Throws it if the sending of the new request throws
     *                          such an exception.
     * @throws InterruptedException Throws it if the sending of the new request throws
     *                                  such an exception.
     */
    public HttpResponse<String> sendRequestWithNoBody(HttpMethod method,
                                             String microserviceAddress,
                                             String pathAndQuery)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .method(method.name(), HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(microserviceAddress + pathAndQuery))
                .build();

        return httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }

    /** Gets the body of the {@link HttpServletRequest} request object.
     *
     * @param request An {@link HttpServletRequest} object the body of which
     *                  will be returned.
     *
     * @return  The body of the {@link HttpServletRequest} request object.
     * @throws IOException  Throws it of one of the used library methods
     *                          throws it.
     */
    public String getRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

    }
}
