package nl.tudelft.sem.controllers;


import java.net.http.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.configuration.Microservice;
import nl.tudelft.sem.requests.senders.HouseIdRequestSender;
import nl.tudelft.sem.requests.senders.RequestSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("application/housemanagement")
public class StudentHouseForwardController extends BaseForwardController {

    private final transient HouseIdRequestSender houseIdRequestSender;

    public StudentHouseForwardController(HouseIdRequestSender houseIdRequestSender,
                                         RequestSender requestSender) {
        super(requestSender);
        this.houseIdRequestSender = houseIdRequestSender;
    }

    /**
     *  Forwards a request to the House Management Microservice, using the received
     * user request and the two path variables of its url address. The method
     * delegates the forwarding to the sendForwardRequest() method of the parent
     * class {@link BaseForwardController}.
     *
     * @param request   The request of the user
     * @param firstVar  First variable of the path
     * @param secondVar Second variable of the path
     * @return Returns a ResponseEntity indicating the response of the microservice
     *              to the forwarded request or that an exception was thrown.
     */
    @RequestMapping("/{firstVar}/{secondVar}")
    public ResponseEntity<String> forwardToHouseManagementService(HttpServletRequest request,
                                                                  @PathVariable String firstVar,
                                                                  @PathVariable String secondVar) {

        return sendForwardRequest(request, firstVar + "/" + secondVar);

    }

    /**
     *  Check JavaDoc of {@link BaseForwardController} for info.
     *
     * @param response       An {@link HttpResponse} object representing the HTTP response
     *                       of the microservice to which a request was forwarded.
     * @param servletRequest An {@link HttpServletRequest} object representing the
     */
    @Override
    protected void processResponse(HttpResponse<String> response,
                                   HttpServletRequest servletRequest) {

        houseIdRequestSender.requestHouseId(servletRequest.getSession());

    }

    @Override
    protected Microservice getMicroservice() {
        return Microservice.HOUSE_MANAGEMENT;
    }

    @Override
    protected String receivedRequestMessage() {
        return "Received request for house management ";
    }
}
