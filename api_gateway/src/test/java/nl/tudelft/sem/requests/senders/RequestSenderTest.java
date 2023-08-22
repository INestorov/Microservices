package nl.tudelft.sem.requests.senders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class RequestSenderTest {


    @Test
    void test_BodyExtraction_NotNullBody() throws IOException {
        String content = "This is the content of the body!";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        String returnedBody = new RequestSender(null).getRequestBody(servletRequest);

        Assertions.assertEquals(returnedBody, content);
    }

    @Test
    void test_BodyExtraction_EmptyBody() throws IOException {
        String content = "";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        String returnedBody = new RequestSender(null).getRequestBody(servletRequest);

        Assertions.assertEquals(returnedBody, content);
    }

    @Test
    void test_BodyExtraction_NullBody() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(null);

        String returnedBody = new RequestSender(null).getRequestBody(servletRequest);

        Assertions.assertEquals(returnedBody, "");
    }


}
