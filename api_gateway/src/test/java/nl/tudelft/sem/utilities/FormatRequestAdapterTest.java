package nl.tudelft.sem.utilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nl.tudelft.sem.requests.adapters.RequestObjectsAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class FormatRequestAdapterTest {

    private transient HttpServletRequest mockRequest;
    private transient HttpSession mockSession;

    @BeforeEach
    void createMockObjects() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockSession = Mockito.mock(HttpSession.class);
    }

    // Set what the methods of the mocked request and session should return
    void setMockRequestObject(String userId,
                              String houseId,
                              List<String> parNames,
                              List<String> parValues) {

        // Set the mock HttpSession object methods
        when(mockSession.getAttribute("userId")).thenReturn(userId);
        when(mockSession.getAttribute("houseId")).thenReturn(houseId);

        List<String> sessionAttrs = new ArrayList<>();
        if (userId != null) {
            sessionAttrs.add(userId);
        }
        if (userId != null && houseId != null) {
            sessionAttrs.add(houseId);
        }
        Enumeration<String> enSessionAttr = Collections.enumeration(sessionAttrs);
        when(mockSession.getAttributeNames()).thenReturn(enSessionAttr);

        // Set the mock HttpServletRequest object
        Enumeration<String> enAttrNames = Collections.enumeration(parNames);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getParameterNames()).thenReturn(enAttrNames);
        for (int i = 0; i < parNames.size(); i++) {
            when(mockRequest.getParameter(parNames.get(i))).thenReturn(parValues.get(i));
        }


    }

    @ParameterizedTest
    @MethodSource("changeMethodArgumentsProvider")
    void test_ChangeMethod(String userId,
                           String houseId,
                           List<String> parNames,
                           List<String> parValue,
                           String method,
                           String body) {

        setMockRequestObject(userId, houseId, parNames, parValue);

        HttpRequest result = RequestObjectsAdapter
                .change(mockRequest,
                        "http://localhost:8000/testpath",
                        null,
                        method,
                        body);

        assertThat(result.method()).isEqualTo(method);
        assertThat(result.bodyPublisher().get().contentLength()).isEqualTo(body.length());


        if (userId != null) {
            assertThat(result.uri().getQuery().contains("userId=" + userId)).isTrue();
        }
        if (userId != null && houseId != null) {
            assertThat(result.uri().getQuery().contains("houseId=" + houseId)).isTrue();
        }

        for (int i = 0; i < parNames.size(); i++) {
            assertThat(result
                    .uri().getQuery()
                    .contains(parNames.get(i) + "=" + parValue.get(i))).isTrue();
        }

    }

    static Stream<Arguments> changeMethodArgumentsProvider() {
        String post = "POST";
        return Stream.of(
                Arguments.of(null, null, List.of(), List.of(), post, ""),
                Arguments.of("1", "3", List.of(), List.of(), post, ""),
                Arguments.of("1", null, List.of(), List.of(), post, ""),
                Arguments.of(null, "3", List.of(), List.of(), post, ""),
                Arguments.of(null, null, List.of("par1", "par2"), List.of("val1", "val2"),
                        post, ""),
                Arguments.of("1", "3", List.of("par1", "par2"), List.of("val1", "val2"),
                        "GET", "Body of request")
                );
    }
}
