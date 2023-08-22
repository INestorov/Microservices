package nl.tudelft.sem.foodservice.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.springframework.lang.NonNull;

@Entity
public class CreditAccount {
    @NonNull
    private float amount;

    @Id
    private int userId = 0;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public CreditAccount() {
    }

    public CreditAccount(int userId, float amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
