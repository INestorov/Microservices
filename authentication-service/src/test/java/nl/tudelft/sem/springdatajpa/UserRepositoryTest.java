package nl.tudelft.sem.springdatajpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.security.auth.DestroyFailedException;
import nl.tudelft.sem.config.DatabaseTestConfig;
import nl.tudelft.sem.exceptions.InvalidInputException;
import nl.tudelft.sem.exceptions.UserConflictException;
import nl.tudelft.sem.model.User;
import nl.tudelft.sem.registration.CheckConflicts;
import nl.tudelft.sem.registration.RegisterUser;
import nl.tudelft.sem.registration.Registrator;
import nl.tudelft.sem.registration.ValidateCredentials;
import nl.tudelft.sem.repositories.UserRepository;
import nl.tudelft.sem.services.UserAuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@DataJpaTest
@ContextConfiguration(
        classes = {DatabaseTestConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Sql(scripts = {"classpath:testing_users.sql"})
@Import({UserAuthenticationService.class, BCryptPasswordEncoder.class})
public class UserRepositoryTest {

    private final transient BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static final String imeNaPesho = "Pesho";

    public static final String mypass1 = "mypass1";

    private final transient User pesho = new User(
            imeNaPesho,
            "security@gmail.com",
            encoder.encode(mypass1));

    private final transient User stranski = new User(
            "Stranski",
            "istranski@gmail.com",
            encoder.encode("iskam_da_spia")
    );

    private final transient User nina = new User(
            "Nina",
            "nina@gmail.com",
            encoder.encode(mypass1)
    );

    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient UserAuthenticationService userAuthenticationService;


    /**
     * Setups the testing database by populating it with users.
     */
    @BeforeEach
    public void setupDatabase() {

        userRepository.save(pesho);

    }

    @Test
    void testIsRepositoryNotNull() {

        assertThat(userRepository).isNotNull();

    }

    @Test
    void testAreUsersSaved() {

        assertThat(userRepository.findAll())
                .hasAtLeastOneElementOfType(User.class);

    }

    @Test
    void testAreAllUsersSaved() {

        assertThat(userRepository.count()).isEqualTo(6);

    }

    @Test
    void testRepositoryFetchesUserSuccessfully() {

        User fetched = userRepository.findByUsername(imeNaPesho);

        assertThat(fetched.getEmail()).isEqualTo(pesho.getEmail());
        assertThat(fetched.getPassword()).isEqualTo(pesho.getPassword());

    }


    /**
     *      Tests for the {@link UserAuthenticationService} class.
     *      Tests for the login method.
     * */
    @Test
    void testLoginSuccessful() {


        Integer savedId = userRepository.findByUsername(imeNaPesho).getId();
        Integer loginId = userAuthenticationService.login(new User(imeNaPesho, mypass1));

        assertThat(savedId).isEqualTo(loginId);

    }


    @Test
    void testLoginFailure_PasswordsDoNotMatch() {

        Integer loginFailed = userAuthenticationService.login(new User(imeNaPesho, "mypass2"));

        assertThat(loginFailed).isNull();

    }

    @Test
    void testLoginFailure_UserDoesNotExist() {

        Integer loginFailed = userAuthenticationService.login(new User("Pesho_Pedal", "mypass2"));

        assertThat(loginFailed).isNull();

    }


    /**
     *  Tests for registerNewUser().
     * */
    @Test
    void testRegistrationIsSuccessfulDifferentPassword() {

        User notExisting = userRepository.findByUsername(stranski.getUsername());
        RegisterUser ru = new RegisterUser();

        Assertions.assertDoesNotThrow(
                () -> {
                    ru.handle(stranski, userRepository, -1);
                }
        );

        User existing = userRepository.findByUsername(stranski.getUsername());

        assertThat(notExisting).isNull();
        assertThat(existing).isNotNull();
    }

    @Test
    void testRegistrationIsSuccessfulWithTheSamePassword() {

        User notExisting = userRepository.findByUsername(nina.getUsername());
        RegisterUser ru = new RegisterUser();

        Assertions.assertDoesNotThrow(
                () -> {
                    ru.handle(nina, userRepository, 1);
                }
        );

        User existing = userRepository.findByUsername(nina.getUsername());

        assertThat(notExisting).isNull();
        assertThat(existing).isNotNull();

    }

    @Test
    void testRegistrationFailure_SameUsername() {

        nina.setUsername("Pesho");
        CheckConflicts cc = new CheckConflicts();

        Exception thrown = Assertions.assertThrows(
                Exception.class,
                () -> {
                    cc.handle(nina, userRepository, -1);
                }
        );

        assertThat(thrown.getMessage()).isEqualTo(
                "Bad credentials: User already exists!");
        assertThat(userRepository.findByEmailOrUsername("Notneeded", nina.getEmail())).isNull();

    }

    @Test
    void testRegistrationFailure_SameEmail() {

        nina.setEmail("security@gmail.com");
        CheckConflicts cc = new CheckConflicts();


        UserConflictException thrown = Assertions.assertThrows(
                UserConflictException.class,
                () -> {
                    cc.handle(nina, userRepository, -1);
                }
        );

        assertThat(thrown.getMessage()).isEqualTo(
                "Bad credentials: User already exists!");
        assertThat(userRepository.findByEmailOrUsername(nina.getUsername(), "Not needed")).isNull();

    }

    /**
     *  Tests for clearDatabase().
     * */
    @Test
    void testClearDatabase() {

        try {

            userAuthenticationService.clearDatabase();

            assertThat(userRepository.count() == 0).isTrue();

        } catch (DestroyFailedException e) {
            System.out.println(e.getMessage());
        }

    }


    /**
     *  Tests for unregisterUser().
     *
     *  */
    @Test
    void testUnregister_UserIsNotFound_WrongUsername() {

        long previousUsers = userRepository.count();
        boolean expectedFalse = userAuthenticationService.unregisterUser(
                new User(pesho.getUsername() + "M", "does not matter"));

        assertThat(expectedFalse).isFalse();
        assertThat(userRepository.count()).isEqualTo(previousUsers);
        assertThat(userRepository.findByUsername(pesho.getUsername())).isNotNull();

    }

    @Test
    void testUnregister_UserIsNotFound_WrongUsername2() {

        long previousUsers = userRepository.count();
        boolean expectedFalse = userAuthenticationService.unregisterUser(
                new User(pesho.getUsername() + " ", "does not matter"));

        assertThat(expectedFalse).isFalse();
        assertThat(userRepository.count()).isEqualTo(previousUsers);
        assertThat(userRepository.findByUsername(pesho.getUsername())).isNotNull();

    }

    @Test
    void testUnregister_WrongPassword() {

        long previousUsers = userRepository.count();
        boolean expectedFalse = userAuthenticationService.unregisterUser(
                new User(pesho.getUsername(), "mywrongpass"));

        assertThat(expectedFalse).isFalse();
        assertThat(userRepository.count()).isEqualTo(previousUsers);
        assertThat(userRepository.findByUsername(pesho.getUsername())).isNotNull();

    }

    @Test
    void testUnregister_WrongPassword2() {

        long previousUsers = userRepository.count();
        boolean expectedFalse = userAuthenticationService.unregisterUser(
                new User(pesho.getUsername(), "mypas"));

        assertThat(expectedFalse).isFalse();
        assertThat(userRepository.count()).isEqualTo(previousUsers);
        assertThat(userRepository.findByUsername(pesho.getUsername())).isNotNull();

    }

    @Test
    void testUnregister_Successful() {

        long previousUsers = userRepository.count();
        User prevPesho = userRepository.findByUsername(pesho.getUsername());
        boolean expectedTrue = userAuthenticationService.unregisterUser(
                new User(pesho.getUsername(), mypass1));

        assertThat(expectedTrue).isTrue();
        assertThat(userRepository.count()).isEqualTo(previousUsers - 1);
        assertThat(prevPesho).isNotNull();
        assertThat(userRepository.findByUsername(pesho.getUsername())).isNull();

    }


    /**
     * Tests for the getIdsForUsernames().
     *
     *  */

    @Test
    void testGetIds_Valid_AllFound() {

        List<String> usernames = List.of(
            "DummyPesho", "DummyPesho2", "DummyPesho3",
                "DummyPesho4", "Dimitar", "Pesho"
        );

        List<Integer> result = Assertions.assertDoesNotThrow(() ->
                userAuthenticationService.getIdsForUsernames(usernames));
        assertThat(result.size()).isEqualTo(usernames.size());

    }

    @Test
    void testGetIds_Valid_OneAndFound() {

        List<String> usernames = List.of("DummyPesho");

        List<Integer> result = Assertions.assertDoesNotThrow(() ->
                userAuthenticationService.getIdsForUsernames(usernames));
        assertThat(result.size()).isEqualTo(usernames.size());

    }

    @Test
    void testThrowsInvalid_NoOneFound() {

        Assertions.assertThrows(InvalidInputException.class, () ->
                userAuthenticationService.getIdsForUsernames(
                        List.of("DummyPesho3", "DummyPesho0", "DummyPesh",
                        "DummyPesho45", "Dimtar", "Peho")
                )
        );

    }

    @Test
    void testThrowsInvalid_OneNotFound() {

        Assertions.assertThrows(InvalidInputException.class, () ->
                userAuthenticationService.getIdsForUsernames(
                        List.of("PeshoMissing")
                )
        );

    }

    @Test
    void testThrowsInvalid_SomePartFound() {

        Assertions.assertThrows(InvalidInputException.class, () ->
                userAuthenticationService.getIdsForUsernames(
                        List.of("DummyPesho3", "DummyPesho2", "DummyPesh",
                        "DummyPesho4", "Dimitar", "Peho")
                )
        );

    }

    @Test
    void encryptTest() {
        String password = "imgreat";
        User user = new User("itsme", password);
        user.encode();
        assertThat(new BCryptPasswordEncoder().matches(password, user.getPassword()));
    }

}
