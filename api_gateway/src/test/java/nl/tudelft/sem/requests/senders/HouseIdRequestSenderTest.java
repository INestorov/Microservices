package nl.tudelft.sem.requests.senders;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;


public class HouseIdRequestSenderTest {

    public static final String houseId = "houseId";

    private transient HouseIdRequestSender testHouseLink;

    private final transient HttpClient httpClient = Mockito.mock(HttpClient.class);
    private final transient HttpResponse<String> httpResponse = Mockito.mock(HttpResponse.class);

    @BeforeEach
    void setMockObjects() throws IOException, InterruptedException {
        testHouseLink = new HouseIdRequestSender(httpClient);

        Mockito.when(httpClient.send(Mockito.any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);
    }

    @Test
    void test_HouseLinkUpdatesHouseId() {


        Mockito.when(httpResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        Mockito.when(httpResponse.body()).thenReturn("new_value");

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockHttpSession.setAttribute(houseId, "value");

        testHouseLink.requestHouseId(mockHttpSession);

        assertThat(mockHttpSession.getAttribute(houseId)).isEqualTo("new_value");

    }

    @Test
    void test_HouseLinkRemovesHouseId() {
        Mockito.when(httpResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.value());
        Mockito.when(httpResponse.body()).thenReturn("whatever");

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockHttpSession.setAttribute(houseId, "value");

        testHouseLink.requestHouseId(mockHttpSession);

        assertThat(mockHttpSession.getAttribute(houseId)).isEqualTo(null);

    }

}
