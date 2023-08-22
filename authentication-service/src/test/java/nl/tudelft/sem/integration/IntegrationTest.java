package nl.tudelft.sem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * This integration test class for the Authentication microservice tests multiple components
 * of the microservice: the AuthenticationController, the UserAuthenticationService, the
 * UserValidator, the UserRepository and its database.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    static final String SIGN_UP_URL = "http://localhost:8001/application/authentication/sign-up";
    static final String securePassword = "\"iAmS02Strong!$\"";
    static final String contentType = "application/json";

    @Autowired
    transient UserRepository repository;

    @Autowired
    transient BCryptPasswordEncoder encoder;

    @Autowired
    transient MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"yupitsmee\", \"email\": \"testemail@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andReturn();

        assertThat(repository.findByUsername("yupitsmee")).isNotNull();
    }

    @Test
    void testMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"yupitsmee2\", \"email\": \"testemail3@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isCreated());

        assertThat(repository.findByUsername("yupitsmee2")).isNotNull();
    }

    @Test
    void testRegisterBadPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"itsanotherone\", \"email\": \"testing@gmail.com\", "
                        + "\"password\": \"Imjusttooe@sy!$\" }")
                .contentType(contentType))
                .andExpect(status().isNotAcceptable());

        assertThat(repository.findByUsername("itsanotherone")).isNull();
    }

    @Test
    void testRegisterSameCreds() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"yupitsmee\", \"email\": \"testemail@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8001/application/authentication/login")
                .content("{\"username\": \"yupitsmee\", \"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8001/application/authentication/login")
                .content("{\"username\": \"yupitsmee\", \"password\": \"ijustguessed!\" }")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unregisterUnexisting() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("http://localhost:8001/application/authentication/unregister")
                .param("username", "idonthaveanaccount")
                .param("password", securePassword)
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unregisterSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"itsmeagaain\", \"email\": \"somany@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isCreated());

        assertThat(repository.findByUsername("itsmeagaain")).isNotNull();

        mockMvc.perform(MockMvcRequestBuilders
                .delete("http://localhost:8001/application/authentication/unregister")
                .content("{ \"username\": \"itsmeagaain\", \"password\": \"iAmS02Strong!$\" }")
                .contentType(contentType))
                .andExpect(status().isOk());

        assertThat(repository.findByUsername("itsmeagaain")).isNull();
    }

    @Test
    void getIdsForUsernamesSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"imnewtothis\", \"email\": \"testemail55@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"imalsosonew\", \"email\": \"testemail2@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isCreated());

        ResultActions res = mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8001/application/authentication/user/get_names")
                .content(" [\"imnewtothis\", \"imalsosonew\"] ")
                .contentType(contentType))
                .andExpect(status().isOk());

        int expected1 = repository.findByEmailOrUsername("", "imalsosonew").getId();
        int expected2 = repository.findByEmailOrUsername("", "imnewtothis").getId();

        assertThat(res.andReturn().getResponse().getContentAsString()).isEqualTo("["
                + expected1 + "," + expected2 + "]");
    }

    @Test
    void getIdsForUsernamesUnexisting() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(SIGN_UP_URL)
                .content("{\"username\": \"idoexistbut\", \"email\": \"testemail33@gmail.com\", "
                        + "\"password\": " + securePassword + " }")
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8001/application/authentication/user/get_names")
                .content(" [\"idoexistbut\", \"ijustdontexist\"] ")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearDatabase() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("http://localhost:8001/application/authentication/reset")
                .contentType(contentType))
                .andExpect(status().isOk());
    }
}
