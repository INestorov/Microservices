package nl.tudelft.sem.foodservice.repositories;

import java.util.Optional;
import nl.tudelft.sem.foodservice.models.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<CreditAccount, Integer> {
    Optional<CreditAccount> findCreditAccountByUserId(int userId);

    void deleteCreditAccountByUserId(int userId);
}
