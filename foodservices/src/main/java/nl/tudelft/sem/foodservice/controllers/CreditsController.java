package nl.tudelft.sem.foodservice.controllers;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.foodservice.models.CreditAccount;
import nl.tudelft.sem.foodservice.repositories.CreditRepository;
import nl.tudelft.sem.foodservice.services.FoodManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Contains the following methods.
 * - getCredits of a given user
 * - add x credits to a given user
 * - remove x credits from a given user
 * - check if credits are below -50, if they are, send notification.
 * (call this method every time you call removeCredits(user))
 * - reset credits by setting credit amount of every user to 0.
 * - testSetup to quickly add credit accounts to the database.
 * - get all credit accounts.
 */
@Controller
@RequestMapping("/application/foodmanagement/credits/")
public class CreditsController {
    final transient FoodManagementService foodManagementService;

    @Autowired
    private transient CreditRepository repository;

    /**
     * Maps the repository to the foodManagementService, where HTML requests are handled.
     *
     * @param foodManagementService The repository handling requests
     */
    public CreditsController(FoodManagementService foodManagementService) {
        this.foodManagementService = foodManagementService;
    }

    /**
     * Returns all CreditAccounts currently in the database, mainly for debug purposes.
     *
     * @return the list of Accounts
     */
    @GetMapping("getAllCreditAccounts")
    @ResponseBody
    public List<CreditAccount> getAllCreditAccounts() {
        return foodManagementService.getCreditRepository().findAll();
    }

    /**
     * Returns the Credits the User has.
     * Possibly calls another method, which should notify the User, when their credits are too low
     *
     * @param userId : userId, the ID of the creditaccount to be returned
     */
    @GetMapping("getCredits")
    @ResponseBody
    public ResponseEntity<String> getCreditById(@RequestParam("userId") int userId) {
        CreditAccount cred = foodManagementService.getCreditRepository()
                .findCreditAccountByUserId(userId).orElse(null);
        //if (cred.getAmount() < -50) { insufficientCredits(cred.getAmount())}
        if (cred == null) {
            return ResponseEntity.badRequest().body("There exists no user with this userId.");
        }
        return ResponseEntity.ok("You have " + cred.getAmount() + " credits. \n"
                + insufficientCredits(cred.getAmount()));
    }

    /**
     * Should add a new account (when new User gets added) with the same Id as UserID.
     * Starting credits for new users is always 0.
     * Returns a Response String to confirm creation.
     *
     * @param userId : userId - userId to create a creditAccount for.
     */
    @PostMapping("addCreditAccount")
    @ResponseBody
    public ResponseEntity<String> addCredits(@RequestParam int userId) {

        CreditAccount creditAccountNew = new CreditAccount(userId, 0);
        foodManagementService.getCreditRepository().save(creditAccountNew);
        return ResponseEntity.ok("successfully added Credit Account. Your current balance is "
                + creditAccountNew.getAmount());

    }

    /**
     * The actual method used to add and remove credits.
     *
     * @param userId the userId to change the credits from.
     * @param amount the amount of credits to add.
     * @return A response, telling the user whether their request worked
     */
    @PostMapping("updateCreditAccount")
    @ResponseBody
    public ResponseEntity<String> updateCredits(@RequestParam("userId") int userId,
                                                @RequestParam("amount") float amount) {
        CreditAccount changedCreditAccount = getCreditAccount(userId);
        if (changedCreditAccount != null) {
            changedCreditAccount.setAmount(changedCreditAccount.getAmount() + amount);
            foodManagementService.getCreditRepository().save(changedCreditAccount);
            return ResponseEntity.ok("The credits have been updated. The new balance is "
                    + changedCreditAccount.getAmount()
                    + ". \n" + insufficientCredits(changedCreditAccount.getAmount()));
        }
        return ResponseEntity.badRequest().body("This credit account does not exist.");
    }

    /**
     * Sets all credit account amounts to 0.
     *
     * @return A response, telling the user whether their request worked.
     */
    @PutMapping("reset")
    @ResponseBody
    public ResponseEntity<String> resetCredits() {
        List<CreditAccount> creditAccounts = foodManagementService.getCreditRepository().findAll();
        for (CreditAccount creditAccount : creditAccounts) {
            creditAccount.setAmount(0);
            foodManagementService.getCreditRepository().save(creditAccount);
        }
        return ResponseEntity.ok("All credit accounts have been reset to 0");
    }

    /**
     * Removes a specific creditAccount of a User.
     *
     * @param userId identifies the user to be deleted
     * @return A response entity letting the user know if their request worked or not
     */
    @DeleteMapping("removeCreditAccount")
    @ResponseBody
    public ResponseEntity<String> removeCreditAccount(@RequestParam("userId") int userId) {
        Optional<CreditAccount> changedCreditAccount = foodManagementService.getCreditRepository()
                .findCreditAccountByUserId(userId);
        if (changedCreditAccount.isPresent()) {
            foodManagementService.getCreditRepository().deleteCreditAccountByUserId(userId);
            return ResponseEntity.ok("The Account has been successfully deleted.");
        } else {
            return ResponseEntity.badRequest().body("There is no Account with that userId");
        }
    }

    /**
     * Only returns a message, notifying the user that their Credits are insufficient.
     */
    public String insufficientCredits(float amount) {

        if (amount < -50) {
            String message = "Your Credits are insufficient. "
                    + "Please go grocery shopping as soon as possible";
            return message;
        } else {
            return "";
        }

    }

    /**
     * Method to get creditAccount of a certain userId.
     * To be used by the updateCredit and getCredit methods.
     *
     * @param userId - the (possible) userId to get the account off.
     * @return the corresponding credit account or null if no such account exists.
     */
    public CreditAccount getCreditAccount(int userId) {
        Optional<CreditAccount> creditAccountOptional = foodManagementService.getCreditRepository()
                .findCreditAccountByUserId(userId);
        return creditAccountOptional.orElse(null);
    }

    /**
     * Method to split a certain number of credits over all people in a house.
     *
     * @param credits number of credits to be split.
     * @param userId  userId, will be used to get all users in the same house.
     */
    public void splitCreditsAllHousemates(float credits, int userId) {
        int[] userIds = getAllUserIdsHouse(userId);
        splitCreditsSomeHousemates(credits, userIds);
    }

    /**
     * Method to split a certain number of credits over specific users.
     *
     * @param credits number of credits to be split.
     * @param userIds array of userIds containing the users to split the costs over.
     */
    public void splitCreditsSomeHousemates(float credits, int[] userIds) {
        for (int userId : userIds) {
            //added variable for clarity
            float creditsPerPerson = credits / userIds.length;
            updateCredits(userId, creditsPerPerson);
        }
    }

    /**
     * Method containing HTTP request to house management to get the housemates of a certain user.
     *
     * @param userId the userId to get the housemates from.
     * @return int[] of userIds of the housemates.
     */
    public int[] getAllUserIdsHouse(int userId) {
        try {
            String urlString = "http://localhost:8003/application/housemanagement"
                    + "/house/gethousemates";
            URL url = new URL(urlString + "?userId=" + userId);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            setUpConnection(con);

            try (BufferedReader in =
                         new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine = in.lines().collect(Collectors.joining());
                con.disconnect();

                Gson gson = new Gson();

                int[] userIds = gson.fromJson(inputLine, int[].class);

                return userIds;

            } catch (IOException ioExcept) {
                return null;
            }

        } catch (IOException mal) {
            return null;
        }

    }

    /**
     * Sets up the connection given an HTTP connection.
     *
     * @param con the connection made with the URL.
     */
    public void setUpConnection(HttpURLConnection con) {
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
    }


}
