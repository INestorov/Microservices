package nl.tudelft.sem.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import nl.tudelft.sem.configuration.HttpMessages;
import nl.tudelft.sem.configuration.Microservice;
import nl.tudelft.sem.requests.senders.HouseIdRequestSender;
import nl.tudelft.sem.requests.senders.RequestSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(RequestSender.class)
public class FoodForwardControllerTest {

    public static final String userId = "userId";

    public static final String testUrl =
            "http://localhost:8000/application/foodmanagement/food/getAllFoodProducts";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HttpClient httpClient;
    @MockBean
    private transient HttpResponse<String> mockResponse;
    @MockBean
    private transient HouseIdRequestSender houseIdRequestSender;
    @InjectMocks
    private transient RequestSender requestSender;

    @InjectMocks
    private transient FoodForwardController foodForwardController;

    @BeforeEach
    void setMockObjects() throws IOException, InterruptedException {
        // Set the mock HttpClient send method
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
    }

    @Test
    void test_MicroserviceUrl_ReturnedCorrectly() {
        assertThat(foodForwardController.getMicroservice().url)
                .isEqualTo(Microservice.FOOD_MANAGEMENT.url);
    }

    @Test
    void test_LoggerMessage_ReturnedCorrectly() {
        assertThat(foodForwardController.receivedRequestMessage())
                .isEqualTo(HttpMessages.FOOD_MANAGEMENT);
    }


    /** Tests the case when a request is received that should be forwarded
     *      to the food management microservice, but there is no session
     *      attribute. In that case, the Gateway returns an error response.
     *
     * @throws Exception Throws it if one of the methods for sending a request
     *      fails.
     */
    @Test
    void test_NoSession_ReturnsError() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(get(testUrl).session(mockHttpSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(HttpMessages.ERROR_NO_LOGIN);
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(400);
                });
        assertThat(mockHttpSession.getAttributeNames().hasMoreElements()).isFalse();
    }


    /** Tests the case when a request is received that should be forwarded
     *      to the food management microservice, and there is a session
     *      attribute. In that case, the Gateway should forward the request
     *      and after that the response from the microservice.
     *
     * @throws Exception Throws it if one of the methods for sending a request
     *      fails.
     */
    @Test
    void test_RequestForwarded_Ok_Response() throws Exception {
        Mockito.when(mockResponse.statusCode()).thenReturn(200);
        Mockito.when(mockResponse.body()).thenReturn("Returned successful body.");

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(userId, "1");

        mockMvc.perform(get(testUrl).session(mockHttpSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Returned successful body.");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.ACCEPTED.value());
                });

        assertThat(mockHttpSession.getAttribute(userId)).isEqualTo("1");
    }

    /** Tests the case when a request is received that should be forwarded
     *      to the food management microservice, and there is a session
     *      attribute. The microservice however returns an error response
     *      and the Gateway should forward this response.
     *
     * @throws Exception Throws it if one of the methods for sending a request
     *      fails.
     */
    @Test
    void test_RequestForwarded_Failure_Response() throws Exception {
        Mockito.when(mockResponse.statusCode()).thenReturn(400);
        Mockito.when(mockResponse.body()).thenReturn("Returned error body.");

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(userId, "1");

        mockMvc.perform(get(testUrl).session(mockHttpSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Returned error body.");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(400);
                });

        assertThat(mockHttpSession.getAttribute(userId)).isEqualTo("1");
    }

    /** Tests the case when a request is received that should be forwarded
     *      to the food management microservice, and there is a session
     *      attribute, but the method for sending a request throws an
     *      exception. In that case, the Gateway should forward the error
     *      message back to the user.
     *
     * @throws Exception Throws it if one of the methods for sending a request
     *      fails.
     */
    @Test
    void test_ExceptionIsThrown() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("IO failure!"));

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(userId, "1");

        mockMvc.perform(get(testUrl).session(mockHttpSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("IO failure!");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_GATEWAY.value());
                });
    }
}
