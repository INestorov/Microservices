package nl.tudelft.sem.foodservice.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.foodservice.models.FoodProduct;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contains the following methods.
 * - create a food product.
 * - delete a food product.
 * - remove or add one or multiple portions from a food product,
 * if 0, delete food product from storage. Credits get updated accordingly.
 * - update a food product (only by the user who created the food product).
 * - testSetup, to quickly add some data to the database.
 * - get all food products in a certain storage
 * - get all food products in the database
 * - delete a spoiled food product.
 * - reset all foodproducts by removing all.
 * - reset all foodproducts in a certain storage by removing all with a certain storageId.
 */

@RestController
@RequestMapping({"/application/foodmanagement/food/"})
public class FoodProductController {
    public static final String FOOD_ID = "foodId";
    public static final String STORAGE_ID = "houseId";
    public static final String USER_ID = "userId";
    final transient FoodManagementService foodManagementService;
    final transient CreditsController creditsController;
    //final transient StorageController storageController;

    /**
     * Connects to another classes in order to ensure functionality.
     *
     * @param foodManagementService hereby we handle our Requests to the repositories
     * @param creditsController     needed in order to use the credit functionality
     */
    public FoodProductController(FoodManagementService foodManagementService,
                                 CreditsController creditsController) {
        this.foodManagementService = foodManagementService;
        this.creditsController = creditsController;
    }

    /**
     * Get all Food in storage.
     *
     * @param storageId id of storage.
     * @return list of all foodProducts in storage or null if the storageID doesn't exist.
     */
    @GetMapping("getStorage")
    public ResponseEntity<String> getFoodInStorage(@RequestParam(STORAGE_ID) int storageId,
                                                   @RequestParam(USER_ID) int userId) {
        Optional<List<FoodProduct>> storageOptional =
                foodManagementService.getFoodProductRepository().findAllBystorageId(storageId);
        if (!storageOptional.isPresent()) {
            return ResponseEntity.badRequest().body("This request did not work. "
                    + "The storage is empty.");
        }

        List<FoodProduct> storage = storageOptional.get();
        String deletedFood = "";
        for (FoodProduct fp : storage) {
            if (fp.isSpoiled() == true) {
                deletedFood = deletedFood + spoiledFoodProduct(storageId, fp.getId(), userId);
            }
        }
        return ResponseEntity.ok(storage.toString() + deletedFood);
    }

    /**
     * Post Mapping to create a foodProduct.
     *
     * @param storageId   the Id of the storage the FoodItem gets added too
     * @param foodProduct the food product to create
     * @return HTTP OK status message
     */
    @PostMapping("addFoodProduct")
    @ResponseBody
    public ResponseEntity<String> addFoodProduct(@RequestParam(STORAGE_ID) int storageId,
                                                 @RequestBody FoodProduct foodProduct,
                                                 @RequestParam(USER_ID) int userId) {
        FoodProduct newFoodProduct = new FoodProduct(
                foodProduct.getName(), foodProduct.getPricePerPortion(),
                foodProduct.getExpirationDate(), foodProduct.getQuantity(),
                foodProduct.getMeasurements(), foodProduct.getPortionSize(),
                userId, storageId);
        if (creditsController.getCreditAccount(userId) == null) {
            creditsController.addCredits(userId);
        }
        creditsController.updateCredits(userId,
                (newFoodProduct.getQuantity() / newFoodProduct.getPortionSize())
                        * newFoodProduct.getPricePerPortion());
        foodManagementService.getFoodProductRepository().save(newFoodProduct);

        return ResponseEntity.ok(newFoodProduct.toString() + " created successfully");
    }

    /**
     * Gets all food Products from all storages.
     *
     * @return ResponseEntity string containing the list of foodproducts.
     */
    @GetMapping("getAllFoodProducts")
    @ResponseBody
    public ResponseEntity<String> getAllFoodProducts() {
        List<FoodProduct> foodProducts = foodManagementService.getFoodProductRepository().findAll();
        if (foodProducts == null) {
            return ResponseEntity.badRequest().body("failed to return all food products.");
        }
        return ResponseEntity.ok("The foodproducts are: " + foodProducts);
    }

    /**
     * Method to take x portions from a certain food product.
     * The user can also fill in a negative amount to 'undo' taking of portions.
     * The credits will then also return to the account,
     * and the foodproduct in the database will get updated.
     *
     * @param foodId the foodId to take the portions from.
     * @param amount the number of portions to take.
     * @return responseEnitity.
     */
    @PutMapping("takePortion")
    @ResponseBody
    public ResponseEntity<String> takePortion(
            @RequestParam(STORAGE_ID) int storageId,
            @RequestParam(FOOD_ID) int foodId,
            @RequestParam("amount") int amount,
            @RequestParam(USER_ID) int userId) {
        return takePortionsGeneral(foodId, new int[]{userId}, amount, storageId);
    }

    /**
     * Combines the take portions methods into one big method.
     *
     * @param foodId    foodId of food to take.
     * @param userIds   users that will take the food. Can also be length 1.
     * @param amount    the number of portions to take.
     * @param storageId the house the user belongs to.
     * @return responseEntity.
     */
    public ResponseEntity<String> takePortionsGeneral(
            int foodId, int[] userIds, int amount, int storageId) {
        FoodProduct foodProduct =
                foodManagementService.getFoodProductRepository().findById(foodId).orElse(null);
        if (foodProduct == null || storageId != foodProduct.getStorageId()) {
            return ResponseEntity.badRequest().body("No food product with this ID exists.");
        }
        float newQuantity = foodProduct.getQuantity()
                - foodProduct.getPortionSize() * amount;

        if (newQuantity < 0) {
            return ResponseEntity.badRequest().body(
                    "There is not enough " + foodProduct.getName() + " left to take "
                            + amount + " portions");
        } else if (newQuantity == 0) {
            deleteFoodProduct(storageId, foodProduct.getId());
        } else {
            foodProduct.setQuantity(newQuantity);
            foodManagementService.getFoodProductRepository().save(foodProduct);
        }
        float cost = foodProduct.getPricePerPortion() * amount;
        creditsController.splitCreditsSomeHousemates(-cost, userIds);
        boolean together = userIds.length > 1;
        String response = " portions from " + foodProduct.getName()
                + ". There are " + newQuantity / foodProduct.getPortionSize()
                + " portions left";

        if (amount >= 0) {
            response = "successfully taken " + amount + response;
            if (together) {
                response = response + " Each user has paid "
                        + cost / userIds.length + " credits.";
            }
        } else {
            response = "successfully put back " + -1 * amount
                    + response;
            if (together) {
                response = response + " Each user has received "
                        + cost / userIds.length + " credits.";
            }
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Method so multiple users can take food together from the storage.
     * The cost will the be split over all these users.
     *
     * @param storageId the storageId of the house.
     * @param foodId    the food to take portions from.
     * @param amount    the number of portions taken.
     * @param userIds   array that contain the userIds of all users taking the food.
     * @return ResponseEntity that will indicate if the action was completed successfully.
     */
    @PutMapping("takePortionTogether")
    @ResponseBody
    public ResponseEntity<String> takePortionTogether(@RequestParam(STORAGE_ID) int storageId,
                                                      @RequestParam(FOOD_ID) int foodId,
                                                      @RequestParam("amount") int amount,
                                                      @RequestBody int[] userIds) {
        return takePortionsGeneral(foodId, userIds, amount, storageId);
    }


    /**
     * Delete food product of a certain ID.
     *
     * @param foodId the id of the food product to delete.
     * @return responseEntity that will confirm/deny action.
     */
    @DeleteMapping("deleteFoodProduct")
    @ResponseBody
    public ResponseEntity<String> deleteFoodProduct(@RequestParam(STORAGE_ID) int storageId,
                                                    @RequestParam(FOOD_ID) int foodId) {
        FoodProduct foodProduct =
                foodManagementService.getFoodProductRepository().findById(foodId).orElse(null);
        if (foodProduct != null && foodProduct.getStorageId() == storageId) {
            foodManagementService.getFoodProductRepository().delete(foodProduct);
            return ResponseEntity.ok(
                    foodProduct.getName() + " successfully deleted.");
        } else {
            return ResponseEntity.badRequest().body("No food product with this ID exists.");
        }
    }

    /**
     * Once a food is spoiled, the User will be notified and Credits will be evenly distributed.
     *
     * @return String, which can be added to a ResponseEntity
     */
    @DeleteMapping("spoiledFoodProduct")
    @ResponseBody
    public String spoiledFoodProduct(@RequestParam(STORAGE_ID) int storageId,
                                     @RequestParam(FOOD_ID) int foodId,
                                     @RequestParam(USER_ID) int userId) {
        FoodProduct spoiledFoodProduct =
                foodManagementService.getFoodProductRepository().findById(foodId).orElse(null);
        if (spoiledFoodProduct == null) {
            return "this food product doesn't exist.";
        }
        float credits = spoiledFoodProduct.getPricePerPortion() * spoiledFoodProduct.getQuantity();
        creditsController.splitCreditsAllHousemates(-credits, userId);
        return deleteFoodProduct(storageId, foodId).getBody() + " because it was spoiled." + '\n'
                + "The remaining " + credits + " have been evenly split over all housemates.";
    }

    /**
     * Deletes all foodproducts in the database.
     */
    @DeleteMapping("reset")
    @ResponseBody
    public ResponseEntity<String> resetFoodProducts() {
        foodManagementService.getFoodProductRepository().deleteAll();
        return ResponseEntity.ok("successfully deleted all foodProducts.");
    }

    /**
     * Deletes all food products in a specific storage.
     *
     * @param storageId id of storage to reset.
     * @return ResponseEntity.
     */
    @DeleteMapping("resetStorage")
    @ResponseBody
    public ResponseEntity<String> resetStorage(@RequestParam(STORAGE_ID) int storageId) {
        List<FoodProduct> storage =
                foodManagementService.getFoodProductRepository()
                        .findAllBystorageId(storageId).orElse(null);
        if (storage == null) {
            return ResponseEntity.badRequest().body("No storage with this ID exists.");
        }
        for (FoodProduct foodProduct : storage) {
            foodManagementService.getFoodProductRepository().delete(foodProduct);
        }
        return ResponseEntity.ok("successfully deleted all foodProducts in storage " + storageId);
    }

    /**
     * Update an existing food product.
     * Users may only update a foodproduct if they were the ones to create it.
     *
     * @param userId      userId of the current session.
     * @param foodId      foodId of the food product to update.
     * @param foodProduct the changed values
     * @return ResponseEntity.
     */
    @PutMapping("updateFoodProduct")
    @ResponseBody
    public ResponseEntity<String> updateFoodProduct(@RequestParam(STORAGE_ID) int storageId,
                                                    @RequestParam(USER_ID) int userId,
                                                    @RequestParam(FOOD_ID) long foodId,
                                                    @RequestBody FoodProduct foodProduct) {
        FoodProduct foodProductOld = foodManagementService
                .getFoodProductRepository().findById((int) foodId).orElse(null);

        if (foodProductOld == null || foodProductOld.getStorageId() != storageId) {
            return ResponseEntity.badRequest().body("This food product doesn't exist.");
        }

        if (foodProductOld.getAddedBy() != userId) {
            return ResponseEntity.badRequest()
                    .body("You did not create this food product, so you may not edit it");
        }

        //remove old price from credits user & add new price
        creditsController.updateCredits(userId,
                -foodProductOld.getPricePerPortion()
                        * foodProductOld.getQuantity() / foodProductOld.getPortionSize());
        creditsController.updateCredits(userId,
                foodProduct.getPricePerPortion()
                        * foodProduct.getQuantity() / foodProduct.getPortionSize());
        foodProductOld.setQuantity(foodProduct.getQuantity());
        foodProductOld.setExpirationDate(foodProduct.getExpirationDate());
        foodProductOld.setMeasurements(foodProduct.getMeasurements());
        foodProductOld.setName(foodProduct.getName());
        foodProductOld.setPortionSize(foodProduct.getPortionSize());
        foodProductOld.setPricePerPortion(foodProduct.getPricePerPortion());

        if (foodProductOld.getQuantity() == 0) {
            foodManagementService.getFoodProductRepository().delete(foodProductOld);
            return ResponseEntity.ok("The given foodProduct has been deleted");
        } else {
            foodManagementService.getFoodProductRepository().save(foodProductOld);
            return ResponseEntity.ok(
                    "The foodproduct has been updated. The new foodproduct is "
                            + foodProductOld.toString());
        }
    }
}
