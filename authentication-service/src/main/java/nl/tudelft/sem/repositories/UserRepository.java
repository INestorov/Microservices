package nl.tudelft.sem.repositories;

import java.util.Collection;
import java.util.List;
import nl.tudelft.sem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {


    User findByEmailOrUsername(String email, String username);

    User findByUsername(String username);

    @Query(value = "SELECT u from User u WHERE u.username IN :usernames")
    List<User> findAllUsersByUsernames(@Param("usernames") Collection<String> usernames);


}
