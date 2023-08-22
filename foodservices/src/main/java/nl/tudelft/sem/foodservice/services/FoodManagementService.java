package nl.tudelft.sem.foodservice.services;

import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.repositories.FoodProductRepository;
import org.springframework.stereotype.Service;

@Service
public class FoodManagementService {

    private transient FoodProductRepository foodProductRepository;
    private transient CreditRepository creditRepository;

    /**
     * Constructor of the Foodmanagement Service.
     *
     * @param foodProductRepository -respective repository for foodProducts
     * @param creditRepository      - respective repository for CreditAccounts
     */
    public FoodManagementService(FoodProductRepository foodProductRepository,
                                 CreditRepository creditRepository) {
        this.foodProductRepository = foodProductRepository;
        this.creditRepository = creditRepository;
    }

    public FoodProductRepository getFoodProductRepository() {
        return foodProductRepository;
    }

    public CreditRepository getCreditRepository() {
        return creditRepository;
    }
}
