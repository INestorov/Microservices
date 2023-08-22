package nl.tudelft.sem.configuration;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayBeansConfig {

    public static final Object lock = new Object();

    /** Configures an HttpClient bean object that will be used by all controllers
     *  for sending requests to the other components of the application.
     *  Its main purpose however is to allow Mock testing of the controller
     *  endpoints.
     *
     * @return The {@link HttpClient} object bean to inject into controllers.
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().build();
    }


}
