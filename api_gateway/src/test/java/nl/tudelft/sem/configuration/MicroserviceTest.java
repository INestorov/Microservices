package nl.tudelft.sem.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MicroserviceTest {


    @Test
    void testConstructor() {
        Microservice testM = Microservice.valueOf("AUTHENTICATION");
        assertThat(testM.url).isEqualTo("http://localhost:8001/application/authentication/");
    }
}
