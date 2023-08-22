package nl.tudelft.sem.config;

import static nl.tudelft.sem.services.SecurityConstants.GETIDS_URL;
import static nl.tudelft.sem.services.SecurityConstants.LOG_IN_URL;
import static nl.tudelft.sem.services.SecurityConstants.RESET_URL;
import static nl.tudelft.sem.services.SecurityConstants.SIGN_UP_URL;
import static nl.tudelft.sem.services.SecurityConstants.UNSIGN_URL;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private transient BCryptPasswordEncoder bcryptPasswordEncoder;

    public WebSecurityConfig(BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    /**
     * Configuration method for spring security. It is set to only allow unauthorized access
     * to the sign-up url.
     *
     * @param http contains the HttpSecurity object
     * @throws Exception if something goes wrong
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST, LOG_IN_URL).permitAll()
                .antMatchers(HttpMethod.DELETE, UNSIGN_URL).permitAll()
                .antMatchers(HttpMethod.POST, GETIDS_URL).permitAll()
                .antMatchers(HttpMethod.DELETE, RESET_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        System.out.println("Configuration done");
    }

}
