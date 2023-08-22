package nl.tudelft.sem.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.springframework.lang.NonNull;

@Entity
public class StudentHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int houseId;

    @NonNull
    @Column(unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "house")
    @Column
    @JsonBackReference
    private List<User> residents = new ArrayList<>();

    /**
     * Constructor of StudentHouse class.
     *
     * @param name name of the house
     */
    public StudentHouse(String name) {
        this.name = name;
    }

    @SuppressWarnings("PMD.NullAssignment")
    public StudentHouse() {
        this.name = null;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer id) {
        this.houseId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getResidents() {
        return residents;
    }

    public void setResidents(List<User> residents) {
        this.residents = residents;
    }

    /**
     * Adds a user to the House (residents).
     *
     * @param user User to be added
     */
    public void addResident(User user) {
        this.getResidents().add(user);
        user.setHouse(this);
    }

    /**
     * Removes a user from the House (enables residents to leave the house).
     *
     * @param user User to be removed
     */
    public void removeResident(User user) {
        if (residents.contains(user)) {
            this.getResidents().remove(user);
            user.setHouse(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentHouse)) {
            return false;
        }
        StudentHouse that = (StudentHouse) o;
        return that.getName().equals(getName())
                && that.getHouseId() == getHouseId()
                && that.getResidents().equals(getResidents());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

}
