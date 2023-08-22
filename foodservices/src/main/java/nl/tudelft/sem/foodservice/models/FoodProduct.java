package nl.tudelft.sem.foodservice.models;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "food_products")
public class FoodProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Column
    private String name;

    @NonNull
    @Column
    private float pricePerPortion;

    @NonNull
    @Column
    private LocalDate expirationDate;

    @NonNull
    @Column
    private float quantity;

    @NonNull
    @Column
    private String measurements;

    @NonNull
    @Column
    private float portionSize;

    @NonNull
    @Column
    private Integer addedBy;

    @NonNull
    @Column
    private int storageId;

    /**
     * Empty constructor.
     */
    protected FoodProduct() {

    }

    /**
     * Constructor foodproduct.
     */
    public FoodProduct(String name, float pricePerPortion, LocalDate expirationDate,
                       float quantity, String measurements, float portionSize,
                       int addedBy, int storageId) {
        this.name = name;
        this.pricePerPortion = pricePerPortion;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.measurements = measurements;
        this.portionSize = portionSize;
        this.addedBy = addedBy;
        this.storageId = storageId;
    }


    public int getId() {
        return id;
    }

    public void setId(int foodId) {
        this.id = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPricePerPortion() {
        return pricePerPortion;
    }

    public void setPricePerPortion(float pricePerPortion) {
        this.pricePerPortion = pricePerPortion;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasurements() {
        return measurements;
    }

    public void setMeasurements(String measurements) {
        this.measurements = measurements;
    }

    public float getPortionSize() {
        return portionSize;
    }

    public void setPortionSize(float portionSize) {
        this.portionSize = portionSize;
    }

    public int getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(int addedBy) {
        this.addedBy = addedBy;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FoodProduct)) {
            return false;
        }
        FoodProduct that = (FoodProduct) o;
        return Float.compare(that.getPricePerPortion(), getPricePerPortion()) == 0
                && Float.compare(that.getPortionSize(), getPortionSize()) == 0
                && getName().equals(that.getName());
    }

    @Override
    public String toString() {
        return "FoodProduct{"
                + "foodId=" + id
                + ", name='" + name + '\''
                + ", pricePerPortion=" + pricePerPortion
                + ", expirationDate='" + expirationDate + '\''
                + ", quantity=" + quantity
                + ", measurements='" + measurements + '\''
                + ", portionSize=" + portionSize
                + ", addedBy=" + addedBy
                + ", storageId=" + storageId
                + "}\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    /**
     * A method that tests whether a foodProduct is spoiled or not, based on the
     * LocalDate.
     *
     * @return boolean, that determines whether the product is spoiled or not
     */
    public boolean isSpoiled() {
        return LocalDate.now().isAfter(expirationDate);
    }
}
