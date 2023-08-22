package nl.tudelft.sem.requests.senders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.servlet.http.HttpSession;
import nl.tudelft.sem.configuration.HttpMessages;
import nl.tudelft.sem.configuration.Microservice;
import nl.tudelft.sem.services.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class HouseIdRequestSender {

    private final transient HttpClient httpClient;

    public HouseIdRequestSender(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /** This method sends a request to the house management service to obtain
     *      the house id of a user. It then saves it in the user's session.
     *
     * @param httpSession   A {@link HttpSession} object representing the user's session.
     */
    public void requestHouseId(HttpSession httpSession) {

        LoggerService.getInstance().logInfo(
                HttpMessages.REQUEST_HOUSE,
                HouseIdRequestSender.class);

        String url = Microservice.HOUSE_MANAGEMENT.url
                + "house/getId?userId=" + httpSession.getAttribute("userId");

        HttpRequest requestForHouseId = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            // Send the request
            HttpResponse<String> response = httpClient.send(
                    requestForHouseId,
                    HttpResponse.BodyHandlers.ofString());

            // Save the obtained house id if no exception was thrown
            if (response.statusCode() == HttpStatus.OK.value()) {
                httpSession.setAttribute("houseId", response.body());
            } else {
                httpSession.removeAttribute("houseId");
            }

        } catch (IOException | InterruptedException e) {
            LoggerService.getInstance().logError(
                    e.getLocalizedMessage(),
                    HouseIdRequestSender.class);
        }

    }

}
