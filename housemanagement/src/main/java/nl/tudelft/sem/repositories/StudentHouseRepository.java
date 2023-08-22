package nl.tudelft.sem.repositories;

import nl.tudelft.sem.models.StudentHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentHouseRepository extends JpaRepository<StudentHouse, Integer> {
    StudentHouse findByName(String houseName);
}
