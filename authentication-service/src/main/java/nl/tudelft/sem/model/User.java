package nl.tudelft.sem.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class User {

    @Transient
    public int serialVersionUid = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column(unique = true)
    private String email;

    private String password;

    /*
        Empty constructor.
     */
    public User() {
    }

    /**
     * Constructs a User.
     *
     * @param userName - Name of the User, that labels them
     * @param email    - Email of the User
     * @param password - Password of the User, needed for authentication
     */
    public User(String userName, String email, String password) {
        this.username = userName;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructs a User without email.
     *
     * @param userName - Name of the User, that labels them
     * @param password - Password of the User, needed for authentication
     */
    public User(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    /**
     * Constructs a User without email.
     *
     * @param userName - Name of the User, that labels them
     * @param password - Password of the User, needed for authentication
     */
    public User(String userName, String password, int id) {
        this.username = userName;
        this.password = password;
        this.id = id;
    }

    public void encode() {
        this.password = new BCryptPasswordEncoder().encode(this.password);
    }

    public boolean matches(String password) {
        return new BCryptPasswordEncoder().matches(password, this.password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
