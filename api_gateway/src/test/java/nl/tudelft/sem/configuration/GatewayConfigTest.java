package nl.tudelft.sem.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient;
import org.junit.jupiter.api.Test;


public class GatewayConfigTest {

    @Test
    void test_BeanIsSuccessfullyCreated() {
        HttpClient httpClient = new GatewayBeansConfig().httpClient();
        assertThat(httpClient).isNotNull();
    }
}
