package nl.tudelft.sem.requests.adapters;

import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseObjectsAdapter {

    /** This method takes a {@link HttpResponse} response object and converts it to
     *      a corresponding {@link ResponseEntity} object using the status code and
     *      the body.
     *
     * @param httpResponse A {@link HttpResponse} object to convert.
     * @return The corresponding {@link ResponseEntity} object.
     */
    public static ResponseEntity<String> createResponse(HttpResponse<String> httpResponse) {

        return ResponseEntity
                .status(httpResponse.statusCode())
                .body(httpResponse.body());
    }
}
