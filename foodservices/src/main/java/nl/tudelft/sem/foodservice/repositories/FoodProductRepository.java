package nl.tudelft.sem.foodservice.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.foodservice.models.FoodProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FoodProductRepository extends JpaRepository<FoodProduct, Integer> {
    List<FoodProduct> findAll();

    Optional<List<FoodProduct>> findAllBystorageId(int storageId);
}

