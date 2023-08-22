package nl.tudelft.sem.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import nl.tudelft.sem.configuration.HttpMessages;
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
public class AuthenticationControllerTest {

    public static final String userId = "userId";
    public static final String houseId = "houseId";

    public static final String loginUrl =
            "http://localhost:8000/application/authentication/login";
    public static final String registerUrl =
            "http://localhost:8000/application/authentication/sign-up";
    public static final String logoutUrl =
            "http://localhost:8000/application/authentication/logout";
    public static final String unregUrl =
            "http://localhost:8000/application/authentication/unregister";

    public static final String loginCreds = "{ \"username\":\"dimitar\", "
            + "\"email\":\"frfr@gmail.com\", \"password\":\"pass\" ";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient HouseIdRequestSender houseIdRequestSender;

    @MockBean
    private transient HttpClient httpClient;
    @MockBean
    private transient HttpResponse<String> mockResponse;

    @Autowired
    private transient RequestSender requestSender;

    @InjectMocks
    private transient AuthenticationForwardController controller;

    @BeforeEach
    void setMockObjects() throws IOException, InterruptedException {
        // Set the mock HttpClient send method
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
    }


    /**
     * Test that if the response from the microservice is not OK, than
     * the controller returns a Bad Request Response with the forwarded error message.
     */
    @Test
    void testRegistration_Failure() throws Exception {

        Mockito.when(mockResponse.statusCode()).thenReturn(400);
        Mockito.when(mockResponse.body()).thenReturn("Register error message");

        mockMvc.perform(post(registerUrl).content(loginCreds))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Register error message");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(400);
                });

    }

    /**
     * Test that if the response from the microservice is OK, than
     * the controller returns an OK response with the new id.
     */
    @Test
    void testRegistration_Successful() throws Exception {

        Mockito.when(mockResponse.statusCode()).thenReturn(200);
        Mockito.when(mockResponse.body()).thenReturn("3");

        mockMvc.perform(post(registerUrl).content(loginCreds))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("3");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.ACCEPTED.value());
                });

    }

    /**
     * Test if the session is empty and the microservice returns
     * an OK response with the user id, then the returned response
     * from the gateway is also an OK response with the right message
     * and the session now contains the user id.
     *
     * @throws Exception Throws it if some of the used methods fail.
     */
    @Test
    void test_LoginSuccessful() throws Exception {

        Mockito.when(mockResponse.statusCode()).thenReturn(200);
        Mockito.when(mockResponse.body()).thenReturn("2");


        MockHttpSession mockSession = new MockHttpSession();
        mockMvc.perform(post(loginUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Login was successful!");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.ACCEPTED.value());
                });

        assertThat(mockSession.getAttribute(userId)).isEqualTo("2");
    }

    /** Test if the microservice returns an error response, then the session
     *      remains untouched and a Bad Request response is returned.
     *
     * @throws Exception Throws it if some of the used methods fails.
     */
    @Test
    void test_LoginFailure_Microservice_Returns_Error() throws Exception {

        Mockito.when(mockResponse.statusCode()).thenReturn(400);
        Mockito.when(mockResponse.body()).thenReturn("Login Failed");

        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post(loginUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Login Failed");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(400);
                });

        assertThat(mockSession.getAttribute(userId)).isNull();

    }

    /** Tests that if there is already an id session attribute when the user
     *      sends a login request, then an error response is returned.
     *
     * @throws Exception Throws it if some of the used methods fails.
     */
    @Test
    void test_LoginFailure_SessionExists() throws Exception {

        // Create an empty session so that login fails
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(userId, "fff");

        mockMvc.perform(post(loginUrl).session(mockSession).content(loginCreds))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(HttpMessages.ERROR_ALREADY_LOGGED_IN);
                });

    }

    /** Tests that if the session has no attribute, the logout request fails
     *      on the gateway and an error response is returned.
     *
     * @throws Exception Throws it if any of the used methods fails.
     */
    @Test
    void test_Logout_Failure_NoSession() throws Exception {
        // Create an empty session so that login fails
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post(logoutUrl).session(mockSession).content("body"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(HttpMessages.ERROR_NO_LOGIN);
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }

    /** Tests of a logout request successfully removes the user session attribute.
     *
     * @throws Exception Throws it if some of the used methods fails.
     */
    @Test
    void test_Logout_Successful_ShouldDeleteSessionAttr() throws Exception {
        // Create an empty session so that login fails
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(userId, "fff");

        mockMvc.perform(post(logoutUrl).session(mockSession).content("body"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(HttpMessages.LOGOUT);
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.ACCEPTED.value());
                });

        assertThat(mockSession.getAttribute(userId)).isNull();
    }

    /** Tests that if the microservice returns an OK response for an
     *  unregister request then the Gateway forwards the response
     *  and deletes the session attribute.
     *
     * @throws Exception Throws it if some of the used methods fails.
     */
    @Test
    void test_Unregister_Successful_ShouldDeleteSessionAttr() throws Exception {

        Mockito.when(mockResponse.statusCode()).thenReturn(200);
        Mockito.when(mockResponse.body()).thenReturn("Response from microservice.");

        // Create an empty session so that login fails
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(userId, "fff");

        mockMvc.perform(post(unregUrl).session(mockSession).content("Unregister body"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Response from microservice.");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.ACCEPTED.value());
                });

        assertThat(mockSession.getAttribute(userId)).isEqualTo(null);

    }

    /** Tests that if the user is a member of a house, the Gateway
     *  does not allow unregistering.
     *
     * @throws Exception Throws it if some of the used methods fails.
     */
    @Test
    void test_Unregister_Failure_UserIsStillInaHouse() throws Exception {

        // Create an empty session so that login fails
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(userId, "f");
        mockSession.setAttribute("houseId", "23");


        mockMvc.perform(post(unregUrl).session(mockSession).content("Unregister body"))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo(HttpMessages.ERROR_CANNOT_UNREGISTER_IN_A_HOUSE);
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

        assertThat(mockSession.getAttribute(userId)).isNotNull();
        assertThat(mockSession.getAttribute("houseId")).isNotNull();


    }


    @Test
    void test_ExceptionIsThrownDuringLogin_Io() throws Exception {

        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Connection failure"));

        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post(loginUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("IO Exception encountered: Connection failure");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_GATEWAY.value());
                });

        assertThat(mockSession.getAttribute(userId)).isNull();

    }

    @Test
    void test_ExceptionIsThrownDuringLogin_Interrupted() throws Exception {

        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Connection failure"));

        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post(loginUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("InterruptedException encountered: Connection failure");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_GATEWAY.value());
                });

        assertThat(mockSession.getAttribute(userId)).isNull();

    }

    @Test
    void test_ExceptionThrownDuringRegister_Io() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Connection failed"));

        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post(registerUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("IOException encountered: Connection failed");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_GATEWAY.value());
                });

        assertThat(mockSession.getAttribute(userId)).isNull();
    }

    @Test
    void test_ExceptionThrownDuringUnRegister_Interrupted() throws Exception {
        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Connection refused"));

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(userId, "1");

        mockMvc.perform(post(unregUrl).content(loginCreds).session(mockSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("InterruptedException encountered: Connection refused");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_GATEWAY.value());
                });

        assertThat(mockSession.getAttribute(userId)).isEqualTo("1");

    }

    @Test
    void test_RequestFails_IllegalParametersFoundByFilter() throws Exception {

        MockHttpSession httpSession = new MockHttpSession();
        httpSession.setAttribute(userId, "2");
        httpSession.setAttribute(houseId, "2");

        mockMvc.perform(post(unregUrl)
                .content(loginCreds)
                .param(userId, "value")
                .session(httpSession))
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("ERROR: Your request contains either"
                                    + " a userId attribute or a houseId attribute, "
                                    + "which are illegal!");
                    assertThat(result.getResponse().getStatus())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

        assertThat(httpSession.getAttribute(userId)).isNotNull();
        assertThat(httpSession.getAttribute(houseId)).isNotNull();
    }
}
