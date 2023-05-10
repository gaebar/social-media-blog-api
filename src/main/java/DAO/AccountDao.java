// Define an interface which declares methods through which the databse will be queried.
// Then, concrete implementation classes can implement the interface and contain the data access
// logic to return the required data.

package DAO;

import Model.Account;
import java.util.List;

public interface AccountDao {
    // Define CRUD (Create, Retrieve, Update, Delete) operations
    Account getAccountById(int id); // Retrieve an Account object by its ID. 
    List<Account> getAllAccounts(); // Retrieve all Account objects in the system.
    boolean insertAccount(Account account); // Insert a new Account object into system. 
    boolean updateAccount(Account account); // Update an existing Account object in the system.
    boolean deleteAccount(int id); // Delete an Account object from system.
}
