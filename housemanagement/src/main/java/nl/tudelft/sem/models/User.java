package nl.tudelft.sem.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.lang.NonNull;

@Entity
@Table
public class User {

    @Transient
    public transient int serialVersionUid = 1;

    @NonNull
    @Id
    private int userId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "houseId")
    @JsonManagedReference
    private StudentHouse house;

    /**
     * Constructs a User.
     *
     * @param id    - Identifying attribute, which will be randomly created
     * @param house - StudentHouse that they belong to
     */
    public User(int id, StudentHouse house) {
        this.userId = id;
        this.house = house;
        this.house.addResident(this);
    }

    public User() {
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public StudentHouse getHouse() {
        return this.house;
    }

    public void setHouse(StudentHouse houseToJoin) {
        this.house = houseToJoin;
    }

    public void moveIn(StudentHouse houseToJoin) {
        houseToJoin.addResident(this);
    }

    @SuppressWarnings("PMD.NullAssignment")
    public void moveOut() {
        this.house.removeResident(this);
    }

    public void addHousemate(User user) {
        user.getHouse().removeResident(user);
        this.getHouse().addResident(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return userId == user.userId && Objects.equals(house, user.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, house);
    }
}
